package test.teamprojecttest;

//ui
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.im.InputContext;

//back
import java.util.*;
import java.io.*;
import java.net.*;


public class PeopleQuiz extends JFrame implements ActionListener, Runnable{
	Container cp;
	JPanel jpR, jpBu, jpPeople, jpCount;
	Font f1 = new Font("나눔고딕 ExtraBold", Font.BOLD, 20);//채팅창
	Font f2 = new Font("나눔고딕 ExtraBold", Font.BOLD, 20);//전광판닉넴
	Font f3 = new Font("나눔고딕 ExtraBold", Font.BOLD, 27);//점수
	JTextArea ja;
	JTextField jtB, jtT;
	JLabel jlPhotoMain, jlPhotoSub, jlPhoto, jlTime, jlTitle, jlBar, jlIu, jImageQ;
	JLabel labelcount, pl1, pl2, pl3, pl4;
	JLabel pl1Waiting, pl2Waiting, pl3Waiting, pl4Waiting;
	JLabel pl1Winner, pl2Winner, pl3Winner, pl4Winner;
	Label pl1Name, pl2Name, pl3Name, pl4Name;
	Label pl1Score, pl2Score, pl3Score, pl4Score;
	JButton jBReady, jBOut;
	ImageIcon mainI, subI, img, img1, bar1;
	JScrollPane scrollPane;
	ImageIcon outIcon = new ImageIcon("imgs\\out.png");
	
	Socket s;
	String nick;
	GameChoiceUI gcu;
	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	Vector<ImageIcon> vQ = new Vector<ImageIcon>();
	int[] random = new int[10];
	String playerIndex="0";  // 몇번째 유저인가
	String playerScore;  // 점수
	String playerName;   // 이름
	String winnerIndex; // 승리자
	boolean gamestart;
	MusicPlayer buttonSE, answerSE, endSE, jing;
	
	public void gt() {
		new Thread(this).start();
	}
	public PeopleQuiz(GameChoiceUI gcu){
		this.gcu = gcu;
		s = gcu.s;
		nick = gcu.nick;
		peopleQuizList();
		init();
		infor();
		new listen().start();
	}
	public void infor() {
		try {
			is = s.getInputStream();
			os = s.getOutputStream();		
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
		}catch (IOException e) {}
	}
	void init(){
		cp = getContentPane();

		jpR = new JPanel();		
		jpR.setBounds(880, 190, 290, 500);			
		jpR.setLayout(null);

		jpPeople = new JPanel();
		jpPeople.setBounds(132,145,450,450);
		jpPeople.setLayout(null);
			
		jpCount = new JPanel();
		jpCount.setBounds(85,75,50,50);
		jpCount.setLayout(null);
		jpCount.setOpaque(false);
		labelcount = new JLabel(new ImageIcon("imgs\\ok.png"));
		labelcount.setBounds(0,0,50,50);
		jpCount.setVisible(false);
		jpCount.add(labelcount);
		
		jtB = new JTextField(5);
		jtT = new JTextField(5);
		ja = new JTextArea(20,5);
		
		jBReady = new JButton(new ImageIcon("imgs\\ready.png"));
		jBReady.setPressedIcon(new ImageIcon("imgs\\ready1.png"));
		jBReady.setFocusPainted(false);
		jBReady.setBorderPainted(false);
		jBReady.setContentAreaFilled(false);
		jBReady.setOpaque(false);
		jBReady.setBounds(640,480,140,160);
		jBReady.addActionListener(this);
		add(jBReady);

		jBOut = new JButton(new ImageIcon("imgs\\out2.png"));
		jBOut.setPressedIcon(new ImageIcon("imgs\\out2.png"));
		jBOut.setFocusPainted(false);
		jBOut.setBorderPainted(false);
		jBOut.setContentAreaFilled(false);
		jBOut.setOpaque(false);
		jBOut.setBounds(0,-15,130,68);
		jBOut.addActionListener(this);
		add(jBOut);

		ImageIcon icon = new ImageIcon("imgs\\sub.png");
		Image img = icon.getImage();
		Image changeImg = img.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
		subI = new ImageIcon(changeImg);
		
		jlPhotoSub = new JLabel(subI);
		jlPhotoSub.setBounds(60,70,600,600);

		jlPhotoMain = new JLabel(new ImageIcon("imgs\\main.png"));
		jlPhotoMain.setBounds(-85,-77,1400,800);

		ImageIcon icon1 = new ImageIcon("imgs\\bar1.png");
		Image img1 = icon1.getImage();
		Image changeImg1 = img1.getScaledInstance(450, 80, Image.SCALE_SMOOTH);
		jlBar = new JLabel(new ImageIcon(changeImg1));
		jlBar.setBounds(360,5,450,80);
		
		ImageIcon icon2 = new ImageIcon("imgs\\question.png");
		Image img2 = icon2.getImage();
		Image changeImg2 = img2.getScaledInstance(450, 450, Image.SCALE_SMOOTH);
		jlIu = new JLabel(new ImageIcon(changeImg2));
		jlIu.setBounds(0,0,450,450);
		jlIu.setBackground(Color.WHITE);
		jlIu.setBorder(new LineBorder(new Color(0, 0, 0), 4, true));
		jpPeople.add(jlIu);
		
		jImageQ = new JLabel();
		jImageQ.setBounds(0,0,450,450);
		jImageQ.setBackground(Color.WHITE);
		jImageQ.setBorder(new LineBorder(new Color(0, 0, 0), 4, true));
		jImageQ.setVisible(false);
		jpPeople.add(jImageQ);
	// 유저 닉네임
		pl1Name = new Label("");
		pl1Name.setBounds(670, 150, 80,20);
		pl1Name.setFont(f2);
		pl1Name.setAlignment(Label.CENTER);
		pl1Name.setBackground(new Color(249, 244, 234));
		pl2Name = new Label("");
		pl2Name.setBounds(795, 150, 80,20);
		pl2Name.setFont(f2);
		pl2Name.setAlignment(Label.CENTER);
		pl2Name.setBackground(new Color(249, 244, 234));
		pl3Name = new Label("");
		pl3Name.setBounds(920, 150, 80,20);
		pl3Name.setFont(f2);
		pl3Name.setAlignment(Label.CENTER);
		pl3Name.setBackground(new Color(249, 244, 234));
		pl4Name = new Label("");
		pl4Name.setBounds(1045, 150, 80,20);
		pl4Name.setFont(f2);
		pl4Name.setAlignment(Label.CENTER);
		pl4Name.setBackground(new Color(249, 244, 234));
		jpR.add(pl1Name); jpR.add(pl2Name); jpR.add(pl3Name); jpR.add(pl4Name);
	// 유저 스코어
		pl1Score = new Label("");
		pl1Score.setBounds(670, 175, 80, 27);
		pl1Score.setFont(f3);
		pl1Score.setAlignment(Label.CENTER);
		pl1Score.setBackground(new Color(249, 244, 234));
		pl1Score.setForeground(Color.RED);
		pl2Score = new Label("");
		pl2Score.setBounds(795, 175, 80, 27);
		pl2Score.setFont(f3);
		pl2Score.setAlignment(Label.CENTER);
		pl2Score.setBackground(new Color(249, 244, 234));
		pl2Score.setForeground(Color.RED);
		pl3Score = new Label("");
		pl3Score.setBounds(920, 175, 80, 27);
		pl3Score.setFont(f3);
		pl3Score.setAlignment(Label.CENTER);
		pl3Score.setBackground(new Color(249, 244, 234));
		pl3Score.setForeground(Color.RED);
		pl4Score = new Label("");
		pl4Score.setBounds(1045, 175, 80, 27);
		pl4Score.setFont(f3);
		pl4Score.setAlignment(Label.CENTER);
		pl4Score.setBackground(new Color(249, 244, 234));
		pl4Score.setForeground(Color.RED);
		jpR.add(pl1Score); jpR.add(pl2Score); jpR.add(pl3Score); jpR.add(pl4Score);
	//winner
		ImageIcon icon8 = new ImageIcon("imgs\\winner.png");
		Image img8 = icon8.getImage();
		Image changeImg8 = img8.getScaledInstance(110,65, Image.SCALE_SMOOTH);
		pl1Winner = new JLabel(new ImageIcon(changeImg8));
		pl1Winner.setBounds(655, 135, 110, 65);
		pl2Winner = new JLabel(new ImageIcon(changeImg8));
		pl2Winner.setBounds(780, 135, 110, 65);
		pl3Winner = new JLabel(new ImageIcon(changeImg8));
		pl3Winner.setBounds(905, 135, 110, 65);
		pl4Winner = new JLabel(new ImageIcon(changeImg8));
		pl4Winner.setBounds(1030, 135, 110, 65);
		jpR.add(pl1Winner); jpR.add(pl2Winner); jpR.add(pl3Winner); jpR.add(pl4Winner); 
	// 유저 대기중
		ImageIcon icon7 = new ImageIcon("imgs\\waiting.png");
		Image img7 = icon7.getImage();
		Image changeImg7 = img7.getScaledInstance(110,65, Image.SCALE_SMOOTH);
		pl1Waiting = new JLabel(new ImageIcon(changeImg7));
		pl1Waiting.setBounds(655, 135, 110, 65);
		pl2Waiting = new JLabel(new ImageIcon(changeImg7));
		pl2Waiting.setBounds(780, 135, 110, 65);
		pl3Waiting = new JLabel(new ImageIcon(changeImg7));
		pl3Waiting.setBounds(905, 135, 110, 65);
		pl4Waiting = new JLabel(new ImageIcon(changeImg7));
		pl4Waiting.setBounds(1030, 135, 110, 65);
		jpR.add(pl1Waiting); jpR.add(pl2Waiting); jpR.add(pl3Waiting); jpR.add(pl4Waiting);
	// 유저 배경
		ImageIcon icon3 = new ImageIcon("imgs\\score1.png");
		Image img3 = icon3.getImage();
		Image changeImg3 = img3.getScaledInstance(120,120, Image.SCALE_SMOOTH);
		ImageIcon icon4 = new ImageIcon("imgs\\score2.png");
		Image img4 = icon4.getImage();
		Image changeImg4 = img4.getScaledInstance(120,120, Image.SCALE_SMOOTH);
		ImageIcon icon5= new ImageIcon("imgs\\score3.png");
		Image img5 = icon5.getImage();
		Image changeImg5 = img5.getScaledInstance(120,120, Image.SCALE_SMOOTH);
		ImageIcon icon6 = new ImageIcon("imgs\\score4.png");
		Image img6 = icon6.getImage();
		Image changeImg6 = img6.getScaledInstance(120,120, Image.SCALE_SMOOTH);
		pl1 = new JLabel(new ImageIcon(changeImg3));
		pl1.setBounds(650, 90, 120, 120);
		pl2 = new JLabel(new ImageIcon(changeImg4));
		pl2.setBounds(775, 90, 120, 120);
		pl3 = new JLabel(new ImageIcon(changeImg5));
		pl3.setBounds(900, 90, 120, 120);
		pl4 = new JLabel(new ImageIcon(changeImg6));
		pl4.setBounds(1025, 90, 120, 120);
		jpR.add(pl1); jpR.add(pl2); jpR.add(pl3); jpR.add(pl4);
	//스크롤
		scrollPane = new JScrollPane(ja);
		scrollPane.setFont(f1);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(660, 220, 470, 240);
		scrollPane.setBorder(null);
		scrollPane.setViewportView(ja);
		scrollPane.setBorder(new LineBorder(new Color(192, 192, 192), 4, true));
		ja.setBackground(new Color(249, 244, 234));
		jpR.add(scrollPane);
	//TextArea
		ja = new JTextArea();		
		ja.setFont(f1);
		ja.setEditable(false);
		ja.setLineWrap(true);
		ja.setBounds(660, 220, 470, 240);
	//입력창
		jtB = new JTextField();
		jtB.setBorder(new LineBorder(new Color(192, 192, 192), 4, true));
		jtB.setBackground(Color.WHITE);
		jtB.addActionListener(this);
		jtB.setBounds(660, 465, 470, 40);
		jpR.add(jtB);
		jtB.setColumns(10);
		jtB.addFocusListener(new FocusAdapter() {   // 채팅창 한글셋팅
	         public void focusGained(FocusEvent e) {
	            InputContext ctx = getInputContext();
	             Character.Subset[] subset = {Character.UnicodeBlock.HANGUL_SYLLABLES};
	             ctx.setCharacterSubsets(subset);
	         }
	      });
		
		jpR.add(jpCount);
		jpR.add(jpPeople);
		jpR.add(jlBar);
		jpR.add(jtT);
		jpR.add(ja);
		jpR.add(jtB);
		jpR.add(jBReady);
		jpR.add(jBOut);
		jpR.add(jlPhotoSub);
		jpR.add(jlPhotoMain);
		
		cp.add(jpR);

		setUI();
	} //init메소드 끝
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == jtB){
			String str = jtB.getText();
			speak(str);
			jtB.setText("");
		}
		if(obj == jBReady){
			gcu.bgm.close();
			buttonSE = new MusicPlayer("bgm\\유후.mp3",false);
			buttonSE.start();
			jBReady.setEnabled(false);
			try {
				dos.writeUTF("@@ready");
				dos.flush();
			} catch (IOException e1) {
			}
		}
		if(obj == jBOut) {
			int answerOut = JOptionPane.showConfirmDialog(null, "종료할까요?", "선택", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, outIcon);
			if(answerOut == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}
	void setUI(){
		setTitle("PeopleQuiz");
		setSize(1200,750);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	@Override
	public void run() {
		game();
	}
	public void game() {
		int rq=0;
		for(int i=0; i<10; i++) {
			jlIu.setVisible(false);
			jImageQ.setVisible(true);
			rq = random[i];
			ImageIcon ii = vQ.get(rq);
			Image img = ii.getImage();
			Image changeImg = img.getScaledInstance(450, 450, Image.SCALE_SMOOTH);
			ImageIcon changeii = new ImageIcon(changeImg);
			JLabel jl = new JLabel(changeii);
			jl.setBounds(0,0,450,450);
			jl.setBorder(new LineBorder(new Color(0, 0, 0), 4, true));
			jpPeople.remove(jImageQ);
			jImageQ = jl;
			jpPeople.add(jImageQ);
			revalidate(); 
			repaint();
			
			for(int c=3; c>0; c--) {
				jpCount.setVisible(true);
				String timeStr = Integer.toString(c);
				ImageIcon icon4 = new ImageIcon("imgs\\"+timeStr+".png");
				Image img4 = icon4.getImage();
				Image changeImg4 = img4.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				ImageIcon changeii4 = new ImageIcon(changeImg4);
				JLabel time = new JLabel(changeii4);
				time.setBounds(0,0,50,50);
				jpCount.remove(labelcount);
				labelcount = time;
				jpCount.add(labelcount);
				revalidate();
				repaint();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
		}
		jpCount.setVisible(false);
		try {
			dos.writeUTF("@@gameend");
			dos.flush();	
		}catch(IOException e) {
		}
		endSE = new MusicPlayer("bgm\\끝.mp3", false);
		endSE.start();
		jlIu.setVisible(true);
		jImageQ.setVisible(false);
		jBReady.setEnabled(true);
	}
// 유저 셋팅
	void updateList() {
		if(Integer.parseInt(playerIndex) == 0) {
			pl1Waiting.setVisible(false);
			pl1Winner.setVisible(false);
			pl1Name.setVisible(true);
			pl1Score.setVisible(true);
			pl1Name.setText(playerName);
			pl1Score.setText(playerScore);
			deleteList();
		}else if(Integer.parseInt(playerIndex) == 1) {
			pl2Waiting.setVisible(false);
			pl2Winner.setVisible(false);
			pl2Name.setVisible(true);
			pl2Score.setVisible(true);
			pl2Name.setText(playerName);
			pl2Score.setText(playerScore);
			deleteList();
		}else if(Integer.parseInt(playerIndex) == 2) {
			pl3Waiting.setVisible(false);
			pl3Winner.setVisible(false);
			pl3Name.setVisible(true);
			pl3Score.setVisible(true);
			pl3Name.setText(playerName);
			pl3Score.setText(playerScore);
			deleteList();
		}else if(Integer.parseInt(playerIndex) == 3) {
			pl4Waiting.setVisible(false);
			pl4Winner.setVisible(false);
			pl4Name.setVisible(true);
			pl4Score.setVisible(true);
			pl4Name.setText(playerName);
			pl4Score.setText(playerScore);
			deleteList();
		}
	}
// 웨이팅 셋팅
	void deleteList() {
		if(Integer.parseInt(playerIndex) == 0) {
			pl2Name.setVisible(false);
			pl2Score.setVisible(false);
			pl3Name.setVisible(false);
			pl3Score.setVisible(false);
			pl4Name.setVisible(false);
			pl4Score.setVisible(false);
			pl2Waiting.setVisible(true);
			pl3Waiting.setVisible(true);
			pl4Waiting.setVisible(true);
			pl2Winner.setVisible(false);
			pl3Winner.setVisible(false);
			pl4Winner.setVisible(false);
		}else if(Integer.parseInt(playerIndex) == 1) {
			pl3Name.setVisible(false);
			pl3Score.setVisible(false);
			pl4Name.setVisible(false);
			pl4Score.setVisible(false);
			pl3Waiting.setVisible(true);
			pl4Waiting.setVisible(true);
			pl3Winner.setVisible(false);
			pl4Winner.setVisible(false);
		}else if(Integer.parseInt(playerIndex) == 2) {
			pl4Name.setVisible(false);
			pl4Score.setVisible(false);
			pl4Waiting.setVisible(true);
			pl4Winner.setVisible(false);
		}
	}
// 위너 셋팅
	void winnerset() {
		if(Integer.parseInt(winnerIndex) == 0) {
			pl1Name.setVisible(false);
			pl1Score.setVisible(false);
			pl1Winner.setVisible(true);
		}else if(Integer.parseInt(winnerIndex) == 1) {
			pl2Name.setVisible(false);
			pl2Score.setVisible(false);
			pl2Winner.setVisible(true);
		}else if(Integer.parseInt(winnerIndex) == 2) {
			pl3Name.setVisible(false);
			pl3Score.setVisible(false);
			pl3Winner.setVisible(true);
		}else if(Integer.parseInt(winnerIndex) == 3) {
			pl4Name.setVisible(false);
			pl4Score.setVisible(false);
			pl4Winner.setVisible(true);
		}
	}
	public void speak(String str) {
		try {
			dos.writeUTF(" : "+str);
			dos.flush();	
		}catch(IOException e) {
		}
	}
// 채팅기능 (내부클래스)
	public class listen extends Thread {
		public void run() {
			String msg="";
			String rqStr="";
			int rq=0;
			try {
				while(true) {
					msg = dis.readUTF();
					if(msg.startsWith("@@업데이트")) {
						playerName = msg.substring(6,msg.indexOf(":"));
						playerScore = msg.substring(msg.indexOf(":")+1, msg.indexOf("#"));
						playerIndex = msg.substring(msg.indexOf("#")+1);
						updateList();
						continue;
					}else if(msg.startsWith("@@승리자")){
						winnerIndex = msg.substring(5,6);
						winnerset();
						continue;
					}
					if(msg.equals("@@randomset")) {
						for(int q=0; q<10; q++) {
							rqStr = dis.readUTF();
							rq = Integer.parseInt(rqStr);
							random[q] = rq;
						}
						continue;
					}
					if(msg.equals("@@정답")) {
						answerSE = new MusicPlayer("bgm\\056_띠동띠동(물음표).mp3",false);
						answerSE.start();
						continue;
					}
					if(msg.equals("@@퇴장 레디초기화")) {
						jBReady.setEnabled(true);
						continue;
					}
					if(msg.equals("@@gamestart")){
						jing = new MusicPlayer("bgm\\징.mp3", false);
						jing.start();
						gt();
					}else {
						ja.append(msg+ "\n");
						ja.setCaretPosition(ja.getDocument().getLength());
						scrollPane.setFont(f1);			
						scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
						scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
						scrollPane.setBounds(660, 220, 470, 240);
						scrollPane.setBorder(null);
						scrollPane.setViewportView(ja);
						ja.setBorder(new LineBorder(new Color(192, 192, 192), 4, true));
						ja.setBackground(new Color(249, 244, 234));
					}
				}
			}catch (IOException e) {
				JOptionPane.showMessageDialog(null, "서버와 연결이 끝어졌습니다", "ERROR!", JOptionPane.WARNING_MESSAGE);
			}finally {
				closeAll();
				System.exit(0);
			}
		}	
	}
// 사진파일 
	public void peopleQuizList(){
		File f = new File("people");
		File[] flist = f.listFiles();
		for(File file2: flist){
			String fName = file2.getName();
			ImageIcon icon = new ImageIcon("people\\"+fName);
			vQ.add(icon);
		}
	}
	void closeAll(){
		try{
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			if(is != null) is.close();
			if(os != null) os.close();
			if(s != null) s.close();
		}catch(IOException ie){}
	}
} // 피플퀴즈 클래스 끝
