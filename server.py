import flask, os, json
from datetime import datetime
from flask import request, jsonify, Flask
from dotenv import load_dotenv
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import String, Integer, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

load_dotenv()

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://{user}:{password}@{hostname}:{port}/{dbname}'.format(
    user=os.getenv('SERVERDBUSER', 'root'),
    password=os.getenv('SERVERDBPASSWORD', '1234'),
    hostname=os.getenv('SERVERDBHOST', 'localhost'),
    port=os.getenv('SERVERDBPORT', '3306'),
    dbname=os.getenv('SERVERDBNAME', 'bookstore')
)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)


class Book(db.Model):
    __tablename__ = 'book'
    book_id: Mapped[str] = mapped_column(String(50), primary_key=True)
    name: Mapped[str] = mapped_column(String(128), nullable=False)
    unit_price: Mapped[int] = mapped_column(Integer, nullable=False)
    author: Mapped[str] = mapped_column(String(64))
    description: Mapped[str] = mapped_column(String(255))
    category: Mapped[str] = mapped_column(String(32))
    release_date: Mapped[str] = mapped_column(String(20)) 

    def to_dict(self):
        return {
            "bookId": self.book_id,
            "name": self.name,
            "unitPrice": self.unit_price,
            "author": self.author,
            "description": self.description,
            "category": self.category,
            "releaseDate": self.release_date
        }

class Order(db.Model):
    __tablename__ = 'orders'
    order_id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    order_date: Mapped[str] = mapped_column(String(20)) # 주문 날짜
    user_name: Mapped[str] = mapped_column(String(50))
    user_phone: Mapped[str] = mapped_column(String(20))
    user_address: Mapped[str] = mapped_column(String(200))

    items = relationship("OrderItem", backref="order", cascade="all, delete-orphan")

class OrderItem(db.Model):
    __tablename__ = 'order_item'
    item_id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    order_id: Mapped[int] = mapped_column(Integer, ForeignKey('orders.order_id'))
    book_id: Mapped[str] = mapped_column(String(50))
    quantity: Mapped[int] = mapped_column(Integer)


with app.app_context():
    db.create_all()

@app.route('/books', methods=['GET'])
def get_book_list():
    try:
        books = db.session.execute(db.select(Book)).scalars().all()
        return jsonify([book.to_dict() for book in books])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/books', methods=['POST'])
def add_book():
    try:
        data = request.get_json()
        
        existing_book = db.session.get(Book, data.get('bookId'))
        if existing_book:
            return jsonify({"message": "Book ID already exists"}), 400

        new_book = Book(
            book_id=data.get('bookId'),
            name=data.get('name'),
            unit_price=int(data.get('unitPrice', 0)),
            author=data.get('author'),
            description=data.get('description'),
            category=data.get('category'),
            release_date=data.get('releaseDate')
        )
        
        db.session.add(new_book)
        db.session.commit()
        
        return jsonify({"message": "success", "book": new_book.to_dict()}), 201
    except Exception as e:
        print(e)
        db.session.rollback()
        return jsonify({"error": str(e)}), 500

@app.route('/orders', methods=['POST'])
def add_order():
    try:
        data = request.get_json()
        
        user_data = data.get('user')
        items_data = data.get('items')

        new_order = Order(
            order_date=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            user_name=user_data.get('name'),
            user_phone=user_data.get('phone'),
            user_address=user_data.get('address')
        )
        db.session.add(new_order)
        db.session.flush() 

        for item in items_data:
            order_item = OrderItem(
                order_id=new_order.order_id,
                book_id=item.get('bookId'),
                quantity=item.get('quantity')
            )
            db.session.add(order_item)

        db.session.commit()
        return jsonify({"message": "Order placed successfully", "orderId": new_order.order_id}), 201

    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=7500, debug=True)
