# schema.py
from typing import List, Union

from pydantic import BaseModel


class ItemBase(BaseModel):
    title: str
    description: Union[str, None] = None


class ItemCreate(ItemBase):
    pass


class Item(ItemBase):
    id: int
    owner_id: int

    class Config:
        orm_mode = True


class UserBase(BaseModel):
    user_id: str
    class Config:
        orm_mode = True

class UserCreate(UserBase):
    password: str


class User(UserBase):
    user_id: str
    is_active: bool
    # items: List[Item] = []

    class Config:
        orm_mode = True
