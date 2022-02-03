package test.teamprojecttest;
//ui
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;
//back
import java.net.*;

public class GameChoiceUI extends JFrame implements ActionListener, Runnable {
	Container cp;
	JPanel p;
	JButton cc, mc; // �ι����̽�, �������̽�
	JLabel jl;
	ImageIcon subI;
	String ip;
	String nick;
	Socket s;
	QLogintest qlt;
	int port = 5000;
	OutputStream os;
	DataOutputStream dos;
	InputStream is;
	DataInputStream dis;
	int kick;
	MusicPlayer bgm;
	ImageIcon errorIcon;

	public GameChoiceUI(QLogintest qlt){
		qlt.dontknow.close();
		this.qlt = qlt;
		init();
		ip = qlt.ip;
		nick = qlt.nick;
		new Thread(this).start();
		bgm = new MusicPlayer("bgm\\ȿ���� �ż����� ȭ����ȯ ȿ����.mp3",false);
		bgm.start();
		errorIcon = new ImageIcon("imgs\\ready2.png");
		try {
			s = new Socket(ip, port);
			is = s.getInputStream();
			dis = new DataInputStream(is);
			os = s.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (UnknownHostException uhe) {
			JOptionPane.showMessageDialog(null, "IP�ּҰ� Ʋ���ų�, ������ �� �����ϴ�. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
			System.exit(0);
		} catch (IOException ie) {
			JOptionPane.showMessageDialog(null, "IP�ּҰ� Ʋ���ų�, ������ �� �����ϴ�. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
			System.exit(0);
		}
	}
	void init(){
		cp = getContentPane();
		
		p = new JPanel();
		p.setLayout(null);

		ImageIcon icon = new ImageIcon("imgs\\choice1.png");
		Image img = icon.getImage();
		Image changeImg = img.getScaledInstance(1200, 750, Image.SCALE_SMOOTH);
		jl = new JLabel(new ImageIcon(changeImg));
		jl.setBounds(0,0,1200,750);

		cc = new JButton(new ImageIcon("imgs\\inmul.png"));
		cc.setPressedIcon(new ImageIcon("imgs\\good.png"));
		cc.setFocusPainted(false);
		cc.setBorderPainted(false);
		cc.setContentAreaFilled(false);
		cc.setOpaque(false);
		cc.setBounds(250,170,300,300);
		cc.addActionListener(this);
		add(cc);

		mc = new JButton(new ImageIcon("imgs\\music.png"));
		mc.setPressedIcon(new ImageIcon("imgs\\ok.png"));
		mc.setFocusPainted(false);
		mc.setBorderPainted(false);
		mc.setContentAreaFilled(false);
		mc.setOpaque(false);
		mc.setBounds(700,195,300,300);
		mc.addActionListener(this);
		add(mc);

		p.add(jl);
		cp.add(p);
		
		setUI();
	} // init �޼ҵ� ��
	void setUI(){
		setTitle("GameChoice");
		setSize(1200, 750);

		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void run() {
		try {
			Thread.sleep(5000);
		}catch(InterruptedException ieee){}
		if(kick == 0){
			closeAll();
			System.exit(0);
		}
	}
	@Override
	public void actionPerformed(ActionEvent e){
		Object obj = e.getSource();
		String systemMsg = "";
		if(obj == cc) {
			try {
				kick++;
				dos.writeUTF("@@�ι�����");  // ������ ��������� �˷���
				systemMsg = dis.readUTF(); // 
				if(systemMsg.equals("@@�����ʰ�")) {
					JOptionPane.showMessageDialog(null, "���� ������ �ʰ��Ǿ��ų�, ������ �������Դϴ�. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
					System.exit(0);
				}
				dos.writeUTF("@@�ι�����");  // ��⿡�� ��������� �˻�
				dos.writeUTF(nick);
				dos.writeUTF("@@�����ʰ� or ���������� �˻�"); //�����ʰ� or �����������϶� ���̻� ������ ������ϰ� ��.
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "���� ������ �ʰ��Ǿ��ų�, ������ �������Դϴ�. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
				System.exit(0);
			} 
			new PeopleQuiz(this);
			mc.setVisible(false);
			setVisible(false);
		}else if(obj == mc) {
			try {
				kick++;
				dos.writeUTF("@@��������");  // ������ ��������� �˷���
				systemMsg = dis.readUTF(); // �������� �����㰡�� �������°� ��ٸ�
				if(systemMsg.equals("@@�����ʰ�")) {
					JOptionPane.showMessageDialog(null, "���� ������ �ʰ��Ǿ��ų�, ������ �������Դϴ�. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
					System.exit(0);
				}
				dos.writeUTF("@@��������");  // ��⿡�� ��������� �˻�
				dos.writeUTF(nick);
				dos.writeUTF("@@�����ʰ� or ���������� �˻�");
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "���� ������ �ʰ��Ǿ��ų�, ������ �������Դϴ�.", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
				System.exit(0);
			}
			new MusicQuiz(this);
			cc.setVisible(false);
			setVisible(false);
		}
	} // �׼Ǹ����� �޼ҵ� ��
	void closeAll(){
		try{
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			if(is != null) is.close();
			if(os != null) os.close();
			if(s != null) s.close();
		}catch(IOException ie){}
	}
	
} // �������̽� Ŭ���� ��
