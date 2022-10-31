# models.py
from sqlalchemy import Boolean, Column, ForeignKey, Integer, String, Date
from sqlalchemy.orm import relationship, declarative_base


import datetime
Base = declarative_base()


class User(Base):
    __tablename__ = "user"
    user_id = Column(String, primary_key=True)
    nickname = Column(String)
    age = Column(Integer)
    # gender = Column(String)
    # home_address = Column(String)
    # comp_address = Column(String)
    # category = Column(String)
    # strength = Column(String)
    # walk_total_count = Column(String)

    # hashed_password = Column(String)
    # is_active = Column(Boolean, default=True)
    # items = relationship("Item", back_populates="owner")
# walkcounts = relationship("WalkCount", back_populates="owner")


# class WalkCount(Base):
#     __tablename__ = "walkcount"

#     walkcount_id = Column(Integer, primary_key=True)

#     person_id = Column(String, primary_key=True,
#                        foreign_key=ForeignKey("user.id"))

#     walk_date = Column(Date, default=datetime.datetime.utcnow)

#     steps = Column(Integer)

#     dist_value = Column(Integer)

#     heart_point = Column(Integer)


# # test data
# class Item(Base):
#     __tablename__ = "items"

#     id = Column(Integer, primary_key=True, index=True)
#     title = Column(String, index=True)
#     description = Column(String, index=True)
#     owner_id = Column(Integer, ForeignKey("users.id"))

#     owner = relationship("User", back_populates="items")
