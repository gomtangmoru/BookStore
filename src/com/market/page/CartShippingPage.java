package com.market.page;

import javax.swing.*;
import java.awt.*;
import com.market.cart.Cart;
import com.market.member.UserInIt;
import com.market.member.User;
import com.market.api.BookApiService;
import java.awt.event.ActionEvent;

public class CartShippingPage extends JPanel {

	Cart mCart;
	JPanel shippingPanel;
	JPanel radioPanel;
	
	JTextField nameField;
	JTextField phoneField;
	JTextField addressField;

	public CartShippingPage(JPanel panel, Cart cart) {

		Font ft;
		ft = new Font("함초롬돋움", Font.BOLD, 15);

		setLayout(null);

		Rectangle rect = panel.getBounds();
		System.out.println(rect);
		setPreferredSize(rect.getSize());

		radioPanel = new JPanel();
		radioPanel.setBounds(300, 0, 700, 50);
		radioPanel.setLayout(new FlowLayout());
		add(radioPanel);
		JLabel radioLabel = new JLabel("배송받을 분은 고객정보와 같습니까?");
		radioLabel.setFont(ft);
		JRadioButton radioOk = new JRadioButton("예");
		radioOk.setFont(ft);
		JRadioButton radioNo = new JRadioButton("아니오");
		radioNo.setFont(ft);
		radioPanel.add(radioLabel);
		radioPanel.add(radioOk);
		radioPanel.add(radioNo);

		shippingPanel = new JPanel();
		shippingPanel.setBounds(200, 50, 700, 500);
		shippingPanel.setLayout(null);
		add(shippingPanel);

		radioOk.setSelected(true);
		radioNo.setSelected(false);
		UserShippingInfo(true);

		this.mCart = cart;

		radioOk.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				if (radioOk.isSelected()) {
					shippingPanel.removeAll();
					UserShippingInfo(true);
					shippingPanel.revalidate();
					shippingPanel.repaint();
					radioNo.setSelected(false);
				}
			}
		});

		radioNo.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				if (radioNo.isSelected()) {
					shippingPanel.removeAll();
					UserShippingInfo(false);
					shippingPanel.revalidate();
					shippingPanel.repaint();
					radioOk.setSelected(false);
				}
			}
		});
	}

	public void UserShippingInfo(boolean select) {

		Font ft;
		ft = new Font("함초롬돋움", Font.BOLD, 15);

		JPanel namePanel = new JPanel();
		namePanel.setBounds(0, 100, 700, 50);
		// namePanel.setBackground(Color.GRAY);
		JLabel nameLabel = new JLabel("고객명 : ");
		nameLabel.setFont(ft);
		namePanel.add(nameLabel);

		nameField = new JTextField(15);
		nameField.setFont(ft);
		if (select) {
			nameField.setBackground(Color.LIGHT_GRAY);
			// nameLabel2.setText("입력된 고객 이름");
			nameField.setText(UserInIt.getmUser().getName());
		}
		namePanel.add(nameField);
		shippingPanel.add(namePanel);

		JPanel phonePanel = new JPanel();
		phonePanel.setBounds(0, 150, 700, 50);
		JLabel phoneLabel = new JLabel("연락처 : ");
		phoneLabel.setFont(ft);
		phonePanel.add(phoneLabel);

		phoneField = new JTextField(15);
		phoneField.setFont(ft);
		if (select) {
			phoneField.setBackground(Color.LIGHT_GRAY);
			// phoneLabel2.setText("입력된 고객 연락처");
			phoneField.setText(String.valueOf(UserInIt.getmUser().getPhone()));
		}
		phonePanel.add(phoneField);
		shippingPanel.add(phonePanel);

		JPanel addressPanel = new JPanel();
		addressPanel.setBounds(0, 200, 700, 50);
		JLabel label = new JLabel("배송지 : ");
		label.setFont(ft);
		addressPanel.add(label);

		addressField = new JTextField(15);
		addressField.setFont(ft);
		if (select) {
			addressField.setBackground(Color.LIGHT_GRAY);
			addressField.setText(UserInIt.getmUser().getAddress());
		}
		addressPanel.add(addressField);
		shippingPanel.add(addressPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds(0, 300, 700, 100);

		JLabel buttonLabel = new JLabel("주문완료");
		buttonLabel.setFont(new Font("함초롬돋움", Font.BOLD, 15));
		JButton orderButton = new JButton();
		orderButton.add(buttonLabel);
		buttonPanel.add(orderButton);
		shippingPanel.add(buttonPanel);

		orderButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				radioPanel.removeAll();

				radioPanel.revalidate();

				radioPanel.repaint();
				shippingPanel.removeAll();
				
				try {
					String name = nameField.getText();
					int phone = Integer.parseInt(phoneField.getText());
					String address = addressField.getText();
					User shippingUser = new User(name, phone, address);
					
					BookApiService.addOrder(shippingUser, mCart.mCartItem);
					
					shippingPanel.add("주문 배송지", new CartOrderBillPage(shippingPanel, mCart, shippingUser));
				} catch (Exception ex) {
					ex.printStackTrace();
					// Fallback if parsing fails or API error
					shippingPanel.add("주문 배송지", new CartOrderBillPage(shippingPanel, mCart, UserInIt.getmUser()));
				}

				mCart.deleteBook();
				shippingPanel.revalidate();
				shippingPanel.repaint();

			}
		});
	}

}