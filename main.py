from typing import Union

from fastapi import FastAPI, Request, Response
from pydantic import BaseModel

from process import getProphetResult

import uvicorn


app = FastAPI()


@app.get("/")
def read_root():
    return {"data": "Hello World!"}


# dummy data
weeksteps = {
    '2022-09-01': 1000,
    '2022-09-02': 2000,
    '2022-09-03': 3000,
    '2022-09-04': 4000,
    '2022-09-05': 5000,
    '2022-09-06': 6000,
    '2022-09-07': 7000,
}


class Steps(BaseModel):
    targetSteps: list


def dictFromItems(items):
    result = {}
    for key, value in items:
        result[key] = value
    return result


# developing
def getStepsLastNDays(amount):
    if amount > len(weeksteps):
        amount = len(weeksteps)
    return {pair[0]: str(pair[1]) for pair in list(weeksteps.items())[-amount:]}


def getStepsByDate(date):
    # if no matched value : -1
    return weeksteps.get(date, -1)


def setStepsByDate(date, steps):
    weeksteps[date] = int(steps)


# 3주치 걸음 수


@app.get("/steps/weeks")
def read_steps_3weeks():
    return getStepsLastNDays(21)


@app.get("/steps/week")
def read_step_week():
    return getStepsLastNDays(7)


@app.get("/steps/all")
def print_steps():
    print("steps/all")
    return weeksteps


@app.get("/steps/{date}")
def read_steps(date):
    return getStepsByDate(date)


@app.post("/steps/day")
async def save_steps_day(steps: Steps):
    steps_list = steps.dict()["targetSteps"]

    if steps_list:
        setStepsByDate(steps_list[0], steps_list[1])
    return f"{getStepsByDate(steps_list[0])} added to {steps_list[0]}"


@app.post("/steps/days")
async def save_steps_days(steps: Steps):

    steps_list = steps.dict()["targetSteps"]
    for i in range(0, len(steps_list), 2):
        _date = steps_list[i]
        _step = steps_list[i+1]
        setStepsByDate(_date, _step)

    return len(weeksteps)


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8080, reload=True)
