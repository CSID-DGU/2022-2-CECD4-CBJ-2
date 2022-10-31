from typing import Optional, Union, List
from pydantic import BaseModel
from sqlalchemy import create_engine, Boolean, Column, ForeignKey, Integer, String, Date
from sqlalchemy.orm import relationship, declarative_base, Session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base

from datetime import datetime, timedelta


Base = declarative_base()



class UserId(BaseModel):
    person_id: str

class Steps(UserId):
    targetSteps: list

class LoginData(UserId):
    password: str


class WalkCount(Base):
    __tablename__ = "walkcount"
    person_id = Column(String, ForeignKey("user.person_id"), primary_key=True)
    walk_date = Column(Date, primary_key=True)
    steps = Column(Integer)
    dist_value = Column(Integer)
    heart_point = Column(Integer)
    parent = relationship("User", back_populates="children")
    # primaryjoin = "WalkCount.person_id==User.person_id"

    def get_personid(self):
        return self.person_id

    def get_steps(self):
        return self.steps

    def get_date(self):
        return self.walk_date.strftime("%Y-%m-%d")


class WalkCountBase(BaseModel):
    person_id: str
    walk_date: datetime
    steps: int
    dist_value: int
    heart_point: int

    class Config:
        orm_mode = True


class WalkScore(Base):
    __tablename__ = "walkscore"
    person_id = Column(String, ForeignKey("user.person_id"), primary_key=True)
    mon = Column(Integer)
    tue = Column(Integer)
    wed = Column(Integer)
    thu = Column(Integer)
    fri = Column(Integer)
    sat = Column(Integer)
    sun = Column(Integer)

    class Config:
        orm_mode = True

    def get_score_by_day(self, day):
        dayOfListDict = {"mon": self.mon, "tue": self.tue, "wed": self.wed,
                         "thu": self.thu, "fri": self.fri, "sat": self.sat, "sun": self.sun}
        return dayOfListDict[day]


class WalkScoreBase(BaseModel):
    mon: int = 3
    tue: int = 3
    wed: int = 3
    thu: int = 3
    fri: int = 3
    sat: int = 3
    sun: int = 3

    class Config:
        orm_mode = True


class User(Base):
    __tablename__ = "user"
    person_id = Column(String, primary_key=True)
    nickname = Column(String)
    age = Column(Integer)
    password = Column(String)
    gender = Column(String)
    home_address = Column(String)
    comp_address = Column(String)
    category = Column(String)
    strength = Column(String)
    walk_total_score = Column(Integer)
    children = relationship("WalkCount", back_populates="parent")


class UserBase(BaseModel):
    person_id: str
    nickname: str
    age: int
    gender: str
    home_addrss: Optional[str] = None
    comp_address: Optional[str] = None
    category: Optional[str] = None
    strength: Optional[str] = None
    walk_total_score: Optional[int] = None

    class Config:
        orm_mode = True


class UserCreate(UserBase):
    password: str
