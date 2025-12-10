package com.market.page;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;

import com.market.api.BookApiService;
import com.market.bookitem.Book;

public class AdminPage extends JPanel {

    public AdminPage(JPanel panel) {

        Font ft = new Font("Apple SD Gothic Neo", Font.BOLD, 15);
        setLayout(null);

        Rectangle rect = panel.getBounds();
        setPreferredSize(rect.getSize());

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
        String strDate = formatter.format(date);

        JPanel idPanel = new JPanel();
        idPanel.setBounds(100, 0, 700, 50);
        JLabel idLabel = new JLabel("도서ID : ");
        idLabel.setFont(ft);
        JLabel idTextField = new JLabel("ISBN" + strDate);
        idTextField.setFont(ft);
        idTextField.setPreferredSize(new Dimension(290, 50));
        idPanel.add(idLabel);
        idPanel.add(idTextField);
        add(idPanel);

        JPanel namePanel = new JPanel();
        namePanel.setBounds(100, 50, 700, 50);
        JLabel nameLabel = new JLabel("도서명 : ");
        nameLabel.setFont(ft);
        JTextField nameTextField = new JTextField(20);
        nameTextField.setFont(ft);
        namePanel.add(nameLabel);
        namePanel.add(nameTextField);
        add(namePanel);

        JPanel pricePanel = new JPanel();
        pricePanel.setBounds(100, 100, 700, 50);
        JLabel priceLabel = new JLabel("가   격 : ");
        priceLabel.setFont(ft);
        JTextField priceTextField = new JTextField(20);
        priceTextField.setFont(ft);
        pricePanel.add(priceLabel);
        pricePanel.add(priceTextField);
        add(pricePanel);

        JPanel authorPanel = new JPanel();
        authorPanel.setBounds(100, 150, 700, 50);
        JLabel authorLabel = new JLabel("저   자 : ");
        authorLabel.setFont(ft);
        JTextField authorTextField = new JTextField(20);
        authorTextField.setFont(ft);
        authorPanel.add(authorLabel);
        authorPanel.add(authorTextField);
        add(authorPanel);

        JPanel descPanel = new JPanel();
        descPanel.setBounds(100, 200, 700, 50);
        JLabel descLabel = new JLabel("설   명 : ");
        descLabel.setFont(ft);
        JTextField descTextField = new JTextField(20);
        descTextField.setFont(ft);
        descPanel.add(descLabel);
        descPanel.add(descTextField);
        add(descPanel);

        JPanel categoryPanel = new JPanel();
        categoryPanel.setBounds(100, 250, 700, 50);
        JLabel categoryLabel = new JLabel("분   야 : ");
        categoryLabel.setFont(ft);
        JTextField categoryTextField = new JTextField(20);
        categoryTextField.setFont(ft);
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryTextField);
        add(categoryPanel);

        JPanel datePanel = new JPanel();
        datePanel.setBounds(100, 300, 700, 50);
        JLabel dateLabel = new JLabel("출판일 : ");
        dateLabel.setFont(ft);
        JTextField dateTextField = new JTextField(20);
        dateTextField.setFont(ft);
        datePanel.add(dateLabel);
        datePanel.add(dateTextField);
        add(datePanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(100, 350, 700, 50);
        add(buttonPanel);

        JLabel okLabel = new JLabel("추가");
        okLabel.setFont(ft);
        JButton okButton = new JButton();
        okButton.add(okLabel);
        buttonPanel.add(okButton);

        // ★★★ book.txt 저장 → 서버로 POST 전송하는 것으로 변경
        okButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Book newBook = new Book(
                            idTextField.getText(),
                            nameTextField.getText(),
                            Integer.parseInt(priceTextField.getText()),
                            authorTextField.getText(),
                            descTextField.getText(),
                            categoryTextField.getText(),
                            dateTextField.getText()
                    );

                    BookApiService.addBook(newBook);

                    JOptionPane.showMessageDialog(okButton, "서버에 도서가 저장되었습니다.");

                    // 초기화
                    Date date = new Date();
                    String newDate = new SimpleDateFormat("yyMMddhhmmss").format(date);
                    idTextField.setText("ISBN" + newDate);
                    nameTextField.setText("");
                    priceTextField.setText("");
                    authorTextField.setText("");
                    descTextField.setText("");
                    categoryTextField.setText("");
                    dateTextField.setText("");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(okButton, "입력값 오류 또는 서버 통신 실패");
                }
            }
        });

        JLabel noLabel = new JLabel("취소");
        noLabel.setFont(ft);
        JButton noButton = new JButton();
        noButton.add(noLabel);
        buttonPanel.add(noButton);

        noButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameTextField.setText("");
                priceTextField.setText("");
                authorTextField.setText("");
                descTextField.setText("");
                categoryTextField.setText("");
                dateTextField.setText("");
            }
        });
    }
}
