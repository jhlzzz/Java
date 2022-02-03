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
	JButton b; //�α��� ��ư
	JPanel p; //��ü �г�
	JLabel la, laN, laI; //la ���ζ� �̹���, laN NAME���� ��, laI IP���� ��
	JTextField taI, taN; //taI IP���� �����ִ� ������, taN NAME�����ִ� ������
	Font f1 = new Font("������� ExtraBold", Font.BOLD, 20);

	String nick;
	String ip;	
	OutputStream os;
	DataOutputStream dos;
	MusicPlayer dontknow;
	
	QLogintest(){
		init();
		dontknow = new MusicPlayer("bgm\\opening.mp3",false);  // bgm ������ �̻��� 
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
				JOptionPane.showMessageDialog(null, "���̵�� IP�� �Է����ּ���!", "Quiz_Error", JOptionPane.QUESTION_MESSAGE, failImg);
			}else if (taN.getText().trim().length()>4){
				JOptionPane.showMessageDialog(null, "ID �ִ� 4�ڱ��� �Է����ּ���!", "Quiz_Error", JOptionPane.QUESTION_MESSAGE, failImg);
				taN.setText("");
			}else{
				nick = taN.getText().trim();
				String temp = taI.getText();
				if(temp.matches("(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$)")){
					ip = temp;
					JOptionPane.showMessageDialog(null, " �α��� ����! ", "JAVA Quiz LOGIN", JOptionPane.INFORMATION_MESSAGE, rightImg);
					new GameChoiceUI(this);
					b.setEnabled(false);
					taN.setEnabled(false);
					taI.setEnabled(false);
					setVisible(false);           
				}else{
					JOptionPane.showMessageDialog(null, "IP �ּҸ� ��Ȯ�ϰ� �Է��ϼ���! ", "ERROR!", JOptionPane.WARNING_MESSAGE, failImg);
				}
			}        
		}
	}
	public static void main(String[] args) {
		new QLogintest();
	}
}

