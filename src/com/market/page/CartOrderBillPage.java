package com.market.page;

import javax.swing.*;

import com.market.bookitem.BookInIt;
import com.market.cart.Cart;
import com.market.member.User;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CartOrderBillPage extends JPanel {

	Cart mCart;
	JPanel shippingPanel;
	JPanel radioPanel;

	public CartOrderBillPage(JPanel panel, Cart cart, User shippingUser) {

		Font ft;
		ft = new Font("Apple SD Gothic Neo", Font.BOLD, 15);

		setLayout(null);

		Rectangle rect = panel.getBounds();
		System.out.println(rect);
		setPreferredSize(rect.getSize());

		this.mCart = cart;

		shippingPanel = new JPanel();
		// shippingPanel.setBounds(200, 50, 700, 500);
		shippingPanel.setBounds(0, 0, 700, 500);
		shippingPanel.setLayout(null);
		// add(shippingPanel);
		panel.add(shippingPanel);
		
		// printBillInfo("입력된 고객 이름", "입력된 고객 연락처", "입력된 고객 배송지");
		printBillInfo(shippingUser.getName(), shippingUser.getPhone(), shippingUser.getAddress());
	}

	public void printBillInfo(String name, String phone, String address) {

		Font ft;
		ft = new Font("Apple SD Gothic Neo", Font.BOLD, 15);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 2);
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String strDate = formatter.format(cal.getTime());

		JLabel label01 = new JLabel("---------------- 배송 받을 고객 정보 ----------------");
		label01.setFont(ft);
		label01.setBounds(0, 0, 700, 30);
		label01.setHorizontalAlignment(JLabel.CENTER);
		shippingPanel.add(label01);

		JLabel labelName = new JLabel("고객명 : " + name);
		labelName.setFont(ft);
		labelName.setBounds(0, 30, 700, 30);
		labelName.setHorizontalAlignment(JLabel.CENTER);
		shippingPanel.add(labelName);

		JLabel labelPhone = new JLabel("연락처 : " + phone);
		labelPhone.setFont(ft);
		labelPhone.setBounds(0, 60, 700, 30);
		labelPhone.setHorizontalAlignment(JLabel.CENTER);
		shippingPanel.add(labelPhone);

		JLabel labelAddress = new JLabel("배송지 : " + address);
		labelAddress.setFont(ft);
		labelAddress.setBounds(0, 90, 700, 30);
		labelAddress.setHorizontalAlignment(JLabel.CENTER);
		shippingPanel.add(labelAddress);

		JLabel labelDate = new JLabel("발송일 : " + strDate);
		labelDate.setFont(ft);
		labelDate.setBounds(0, 120, 700, 30);
		labelDate.setHorizontalAlignment(JLabel.CENTER);
		shippingPanel.add(labelDate);

		JPanel printPanel = new JPanel();
		printPanel.setBounds(0, 160, 700, 400);
		printPanel.setLayout(null);
		printCart(printPanel);
		shippingPanel.add(printPanel);
	}

	public void printCart(JPanel panel) {

		Font ft;
		ft = new Font("Apple SD Gothic Neo", Font.BOLD, 12);

		JLabel label01 = new JLabel("장바구니 상품 목록 :");
		label01.setFont(ft);
		label01.setBounds(0, 0, 700, 30);
		label01.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label01);

		JLabel label02 = new JLabel("------------------------------------------------");
		label02.setFont(ft);
		label02.setBounds(0, 30, 700, 30);
		label02.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label02);

		JLabel label03 = new JLabel("도서ID           |           수량           |           합계");
		label03.setFont(ft);
		label03.setBounds(0, 60, 700, 30);
		label03.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label03);

		JLabel label04 = new JLabel("------------------------------------------------");
		label04.setFont(ft);
		label04.setBounds(0, 90, 700, 30);
		label04.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label04);

		for (int i = 0; i < mCart.mCartItem.size(); i++) {
			JLabel label05 = new JLabel(mCart.mCartItem.get(i).getBookID() + "           |           "
					+ mCart.mCartItem.get(i).getQuantity() + "           |           "
					+ mCart.mCartItem.get(i).getTotalPrice() + "원");
			label05.setFont(ft);
			label05.setBounds(0, 120 + (i * 30), 700, 30);
			label05.setHorizontalAlignment(JLabel.CENTER);
			panel.add(label05);
		}

		JLabel label06 = new JLabel("------------------------------------------------");
		label06.setFont(ft);
		label06.setBounds(0, 120 + (mCart.mCartItem.size() * 30), 700, 30);
		label06.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label06);

		int sum = 0;

		for (int i = 0; i < mCart.mCartItem.size(); i++)
			sum += mCart.mCartItem.get(i).getTotalPrice();

		JLabel label07 = new JLabel("주문 총금액 : " + sum + "원");
		label07.setFont(new Font("Apple SD Gothic Neo", Font.BOLD, 15));
		label07.setBounds(0, 150 + (mCart.mCartItem.size() * 30), 700, 30);
		label07.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label07);

	}

	public static void main(String[] args) {

		Cart mCart = new Cart();
		JFrame frame = new JFrame();
		frame.setBounds(0, 0, 1000, 750);
		frame.setLayout(null);

		JPanel mPagePanel = new JPanel();
		mPagePanel.setBounds(0, 150, 1000, 750);

		frame.add(mPagePanel);
		BookInIt.init();
		User dummyUser = new User("Test User", "01012345678", "Seoul, Korea");
		mPagePanel.add("주문하기", new CartOrderBillPage(mPagePanel, mCart, dummyUser));
		frame.setVisible(true);

	}
}