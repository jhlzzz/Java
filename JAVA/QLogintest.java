package test.teamprojecttest;

import java.net.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.im.InputContext;

public class QLogintest extends JFrame implements ActionListener {
	Container cp;
	JButton b; //로그인 버튼
	JPanel p; //전체 패널
	JLabel la, laN, laI; //la 메인라벨 이미지, laN NAME글자 라벨, laI IP글자 라벨
	JTextField taI, taN; //taI IP글자 쓸수있는 에리아, taN NAME쓸수있는 에리아
	Font f1 = new Font("나눔고딕 ExtraBold", Font.BOLD, 20);

	String nick;
	String ip;	
	OutputStream os;
	DataOutputStream dos;
	MusicPlayer dontknow;
	
	QLogintest(){
		init();
		dontknow = new MusicPlayer("bgm\\opening.mp3",false);  // bgm 파일이 이상함 
		dontknow.start();
	}
	void init(){
		cp = getContentPane();

		p = new JPanel();
		p.setLayout(null);

		taI = new JTextField("127.0.0.1");
		taI.setBorder(new LineBorder(new Color(235, 150, 5), 3, true));
		taI.setFont(f1);
		taI.setBackground(Color.WHITE);
		taI.setColumns(12);
		taI.setBounds(500, 495, 250, 40);
		taI.addActionListener(this);
		p.add(taI);
		
		taN = new JTextField();
		taN.setBorder(new LineBorder(new Color(235, 150, 5), 3, true));
		taN.setBackground(Color.WHITE);
		taN.setFont(f1);
		taN.setColumns(30);
		taN.setBounds(310, 495, 130, 40);
		taN.addActionListener(this);
		p.add(taN);

		b = new JButton(new ImageIcon("imgs/lo1.png"));
		b.setPressedIcon(new ImageIcon("imgs/lo2.png"));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setOpaque(false);
		b.setBounds(779, 485, 100, 50);
		b.addActionListener(this);
		p.add(b);
		
		laN = new JLabel(new ImageIcon("imgs/name.png"));
		laN.setBounds(220, 500, 85, 35);
		p.add(laN);

		laI = new JLabel(new ImageIcon("imgs/ip.png"));
		laI.setBounds(438, 500, 85, 35);
		p.add(laI);

		la = new JLabel(new ImageIcon("imgs/login.png"));
		la.setBounds(-150, -50, 1450, 800);
		p.add(la);
		
		cp.add(p);
		setUI();
	}
	void setUI(){
		setTitle("Login");
		setSize(1150, 750);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent e){
		ImageIcon failImg = new ImageIcon("imgs\\lo1.png");
		ImageIcon rightImg = new ImageIcon("imgs\\lo2.png");
		Object obj = e.getSource();
		if(obj == b || obj == taN || obj == taI){
			if (taN.getText().equals("") || taI.getText().equals("")){         
				JOptionPane.showMessageDialog(null, "아이디와 IP를 입력해주세요!", "Quiz_Error", JOptionPane.QUESTION_MESSAGE, failImg);
			}else if (taN.getText().trim().length()>4){
				JOptionPane.showMessageDialog(null, "ID 최대 4자까지 입력해주세요!", "Quiz_Error", JOptionPane.QUESTION_MESSAGE, failImg);
				taN.setText("");
			}else{
				nick = taN.getText().trim();
				String temp = taI.getText();
				if(temp.matches("(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$)")){
					ip = temp;
					JOptionPane.showMessageDialog(null, " 로그인 성공! ", "JAVA Quiz LOGIN", JOptionPane.INFORMATION_MESSAGE, rightImg);
					new GameChoiceUI(this);
					b.setEnabled(false);
					taN.setEnabled(false);
					taI.setEnabled(false);
					setVisible(false);           
				}else{
					JOptionPane.showMessageDialog(null, "IP 주소를 정확하게 입력하세요! ", "ERROR!", JOptionPane.WARNING_MESSAGE, failImg);
				}
			}        
		}
	}
	public static void main(String[] args) {
		new QLogintest();
	}
}

