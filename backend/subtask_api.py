from fastapi import APIRouter
from pydantic import BaseModel
from openai import OpenAI
import os
import json

router = APIRouter(
    prefix="/tasks",
    tags=["SubTasks"]
)


client = OpenAI(
    base_url=os.getenv(
        "LLM_BASE_URL",
        "http://localhost:1234/v1"
    ),
    api_key=os.getenv(
        "LLM_API_KEY",
        "lm-studio"
    )
)

MODEL_NAME = os.getenv(
    "LLM_MODEL",
    "deepseek-r1-distill-qwen-7b"
)


class GenerateSubTasksRequest(BaseModel):

    title: str
    description: str = ""
    duration_minutes: int
    priority: int


class SubTaskResponse(BaseModel):

    title: str
    description: str


class GenerateSubTasksResponse(BaseModel):

    subtasks: list[SubTaskResponse]


@router.post(
    "/generate-subtasks",
    response_model=GenerateSubTasksResponse
)
def generate_subtasks(
    data: GenerateSubTasksRequest
):
    print("\n========== SUBTASK REQUEST ==========")
    print("Title:", data.title)
    print("Description:", data.description)
    print("Duration:", data.duration_minutes)
    print("Priority:", data.priority)
    print("=====================================\n")

    prompt = f"""
You are an intelligent task-planning assistant.

Your job is to break the following task into a small number of logical,
practical, and actionable subtasks.

IMPORTANT LANGUAGE RULE:
- The input may be in Persian or English.
- ALL generated subtask titles MUST be written in Persian.
- ALL generated subtask descriptions MUST be written in Persian.
- Do NOT generate English text in the subtasks.
- Do NOT translate the task into English.
- The final JSON values for "title" and "description" must be Persian.

Task title:
{data.title}

Task description:
{data.description}

Total task duration:
{data.duration_minutes} minutes

Priority:
{data.priority}

Rules:

1. Create practical and clearly actionable subtasks.
2. Do not make the subtasks unnecessarily small.
3. Choose the number of subtasks based on the complexity of the task.
4. For a simple task, generate only a few subtasks.
5. Each subtask must have a short Persian title and a short Persian description.
6. Do NOT assign time or duration to individual subtasks.
7. Return ONLY valid JSON.
8. Do not add Markdown.
9. Do not add explanations before or after the JSON.

The output format MUST be exactly:

{{
    "subtasks": [
        {{
            "title": "عنوان فارسی زیرکار",
            "description": "توضیح کوتاه فارسی درباره زیرکار"
        }}
    ]
}}
"""

    response = client.chat.completions.create(
        model=MODEL_NAME,
        messages=[
            {
                "role": "system",
                "content": (
                    "You are a professional task decomposition assistant. "
                    "You MUST generate all subtask titles and descriptions in Persian. "
                    "Return valid JSON only."
                )
            },
            {
                "role": "user",
                "content": prompt
            }
        ],
        temperature=0.3
    )

    content = response.choices[0].message.content.strip()

    if content.startswith("```"):
        content = content.replace("```json", "").replace("```", "").strip()

    result = json.loads(content)

    return result