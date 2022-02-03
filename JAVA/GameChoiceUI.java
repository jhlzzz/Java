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
	JButton cc, mc; // 인물초이스, 뮤직초이스
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
		bgm = new MusicPlayer("bgm\\효과음 신서유기 화면전환 효과음.mp3",false);
		bgm.start();
		errorIcon = new ImageIcon("imgs\\ready2.png");
		try {
			s = new Socket(ip, port);
			is = s.getInputStream();
			dis = new DataInputStream(is);
			os = s.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (UnknownHostException uhe) {
			JOptionPane.showMessageDialog(null, "IP주소가 틀리거나, 입장할 수 없습니다. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
			System.exit(0);
		} catch (IOException ie) {
			JOptionPane.showMessageDialog(null, "IP주소가 틀리거나, 입장할 수 없습니다. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
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
	} // init 메소드 끝
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
				dos.writeUTF("@@인물퀴즈");  // 서버에 어떤퀴즈인지 알려줌
				systemMsg = dis.readUTF(); // 
				if(systemMsg.equals("@@정원초과")) {
					JOptionPane.showMessageDialog(null, "입장 정원이 초과되었거나, 게임이 진행중입니다. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
					System.exit(0);
				}
				dos.writeUTF("@@인물퀴즈");  // 모듈에서 어떤퀴즈인지 검사
				dos.writeUTF(nick);
				dos.writeUTF("@@정원초과 or 게임진행중 검사"); //정원초과 or 게임진행중일때 더이상 유저가 입장못하게 함.
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "입장 정원이 초과되었거나, 게임이 진행중입니다. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
				System.exit(0);
			} 
			new PeopleQuiz(this);
			mc.setVisible(false);
			setVisible(false);
		}else if(obj == mc) {
			try {
				kick++;
				dos.writeUTF("@@음악퀴즈");  // 서버에 어떤퀴즈인지 알려줌
				systemMsg = dis.readUTF(); // 서버에서 입장허가가 떨어지는걸 기다림
				if(systemMsg.equals("@@정원초과")) {
					JOptionPane.showMessageDialog(null, "입장 정원이 초과되었거나, 게임이 진행중입니다. ", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
					System.exit(0);
				}
				dos.writeUTF("@@음악퀴즈");  // 모듈에서 어떤퀴즈인지 검사
				dos.writeUTF(nick);
				dos.writeUTF("@@정원초과 or 게임진행중 검사");
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "입장 정원이 초과되었거나, 게임이 진행중입니다.", "ERROR!", JOptionPane.WARNING_MESSAGE, errorIcon);
				System.exit(0);
			}
			new MusicQuiz(this);
			cc.setVisible(false);
			setVisible(false);
		}
	} // 액션리스너 메소드 끝
	void closeAll(){
		try{
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			if(is != null) is.close();
			if(os != null) os.close();
			if(s != null) s.close();
		}catch(IOException ie){}
	}
	
} // 게임초이스 클래스 끝
