import flask, hashlib, os, json
from datetime import date
from flask import request, jsonify, Flask
from dotenv import load_dotenv
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column
from sqlalchemy import String, Integer, Date, select
from flask_sqlalchemy import SQLAlchemy


load_dotenv()

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://{user}:{password}@{hostname}:{port}/{dbname}'.format(
    user = os.getenv('SERVERDBUSER'),
    password = os.getenv('SERVERDBPASSWORD'),
    hostname = os.getenv('SERVERDBHOST'),
    port = os.getenv('SERVERDBPORT'),
    dbname = os.getenv('SERVERDBNAME')
)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

class BookTable(db.Model):
    __tablename__ = 'BookTable'
    BookID: Mapped[str] = mapped_column(String(20), nullable=False, primary_key=True)
    Title: Mapped[str] = mapped_column(String(128))
    Price: Mapped[int] = mapped_column(Integer)
    Author: Mapped[str] = mapped_column(String(10))
    Description: Mapped[str] = mapped_column(String(128))
    Tag: Mapped[str] = mapped_column(String(20))
    ReleasedDate: Mapped[date] = mapped_column(Date)
    def callDict(self):
        return {
            "Bookid" : self.BookID,
            "Title": self.Title,
            "Price": self.Price,
            "Author": self.Author,
            "Description": self.Description,
            "Tag": self.Tag,
            "ReleasedDate": self.ReleasedDate
        }

class UserTable(db.Model):
    __tablename__ = 'UserTable'
    UserID: Mapped[int] = mapped_column(Integer, primary_key=True)
    UserName: Mapped[str] = mapped_column(String(25))
    PassToken: Mapped[str] = mapped_column(String(128))

with app.app_context():
    db.create_all()



@app.route('/api/book/list', methods=['GET'])
def BookList() -> json:
    query = select(BookTable)
    rs1 = db.session.execute(query).scalars().all()
    data = [BookTable.callDict() for BookTable in rs1]
    print(data)
    response = {
        "data" : data
    }
    return jsonify({"data" : data})



if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5600);

