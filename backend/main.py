from fastapi import FastAPI

from subtask_api import router as subtask_router


app = FastAPI(
    title="AIPlanner Backend"
)


app.include_router(
    subtask_router
)


@app.get("/")
def root():
    return {
        "status": "ok",
        "message": "AIPlanner Backend is running"
    }