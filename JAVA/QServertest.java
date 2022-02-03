package test.teamprojecttest;
//ui
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

//back
import java.net.*;
import java.util.*;
import java.io.*;

public class QServertest extends JFrame implements ActionListener, Runnable {
	Container cp;
	JPanel p;
	JTextArea ta;
	JLabel la, laM, laS;
	JButton bOn, bOff;
	JScrollPane sp;
	
	ServerSocket ss;
	int port = 5000;
	Socket s;
	QOneClientModuletest qocm;
	Vector <QOneClientModuletest> vP = new Vector <QOneClientModuletest>();
	Vector <QOneClientModuletest> vM = new Vector <QOneClientModuletest>();
	Vector<String> vAPeopleQ = new Vector<String>();
	Vector<String> vAMusicQ = new Vector<String>();
	int peopleReady, musicReady;
	boolean peopleGameStart, musicGameStart;
	String peopleAnswer, musicAnswer;
	Vector<String> peoplepPlayInfoName = new Vector<String>();
	Vector<String> musicPlayInfoName = new Vector<String>();
	public static int[] peoplePlayInfoScore, musicPlayInfoScore;
	boolean peopleAnswerExist, musicAnswerExist;
	int skipNum;
	OutputStream os;
	DataOutputStream dos;
	InputStream is;
	DataInputStream dis;

	public QServertest() {
		peopleAnswerList();
		musicAnswerList();
		init();
	}
	void init(){
		cp = getContentPane();

		p = new JPanel();
		p.setLayout(null);

		ta = new JTextArea();
		ta.setBorder(new LineBorder(new Color(200, 200, 200), 2, true));
		ta.setBackground(Color.WHITE);
		ta.setEditable(false);
		ta.setColumns(5000);
		ta.setBounds(44, 75, 300, 240);
		p.add(ta);
		
		sp = new JScrollPane(ta);
		ta.setCaretPosition(ta.getDocument().getLength());			
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBounds(44, 75, 300, 240);
		sp.setBorder(null);
		sp.setViewportView(ta);
		p.add(sp);
		
		bOn = new JButton();
		bOn = new JButton(new ImageIcon("imgs\\on1.png"));
		bOn.setPressedIcon(new ImageIcon("imgs\\on2.png"));
		bOn.setFocusPainted(false);
		bOn.setBorderPainted(false);
		bOn.setContentAreaFilled(false);
		bOn.setOpaque(false);
		bOn.setBounds(70, 320, 100, 50);
		p.add(bOn);
		bOn.addActionListener(this);

		bOff = new JButton();
		bOff = new JButton(new ImageIcon("imgs\\off1.png"));
		bOff.setPressedIcon(new ImageIcon("imgs\\off2.png"));
		bOff.setFocusPainted(false);
		bOff.setBorderPainted(false);
		bOff.setContentAreaFilled(false);
		bOff.setOpaque(false);
		bOff.setBounds(220, 320, 100, 50);
		bOff.setEnabled(false);
		p.add(bOff);
		bOff.addActionListener(this);

		laM = new JLabel(new ImageIcon("imgs\\serverfont.png"));
		laM.setForeground(new Color(240, 40, 5));	
		laM.setBounds(-115, -255, 590, 590);
		p.add(laM);

		laS = new JLabel(new ImageIcon("imgs\\sm.png"));
		laS.setBounds(310, 5, 65, 65);
		p.add(laS);

		la = new JLabel(new ImageIcon("imgs\\servermain.png"));
		la.setBounds(-20, -20, 420, 520);
		p.add(la);

		cp.add(p);
		setUI();
	} // init �޼ҵ� ��
	void setUI(){
		setTitle("shinseoyouquizServer");
		setSize(400, 500);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
//��������
	@Override
	public void run() {
		while(true) {
			String gameChoice ="";
			try {
				s = ss.accept();
				is = s.getInputStream();
				dis = new DataInputStream(is);
				os = s.getOutputStream();
				dos = new DataOutputStream(os);
				
				gameChoice = dis.readUTF();   // �����ʰ� �˻�
				
				if(gameChoice.equals("@@�ι�����")) {
					if(vP.size() > 3 || peopleGameStart == true) {  // v.size() > 1 �� ���ǿ� ���ִ� ���ں���  +1 ��ŭ ���Ӱ��� (3 ������ 4����� ����)
						dos.writeUTF("@@�����ʰ�");
					}else {
						dos.writeUTF("@@���尡��");
						qocm = new QOneClientModuletest(this);
						vP.add(qocm);
						qocm.start();
					}
				}else if(gameChoice.equals("@@��������")) {
					if(vM.size() > 3 || musicGameStart == true) {  // v.size() > 1 �� ���ǿ� ���ִ� ���ں���  +1 ��ŭ ���Ӱ��� (3 ������ 4����� ����)
						dos.writeUTF("@@�����ʰ�");
					}else {
						dos.writeUTF("@@���尡��");
						qocm = new QOneClientModuletest(this);
						vM.add(qocm);
						qocm.start();
					}
				}
			} catch (IOException e) {
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == bOn){
				ta.append(" [ ������ ���۵Ǿ����ϴ�. ]" + "\n");
				try {
					ss = new ServerSocket(port);
					bOn.setEnabled(false);
					bOff.setEnabled(true);
					Thread th = new Thread(this);
					th.start();	
				}catch(IOException ie) {}
		}else if(e.getSource() == bOff){
			try {
				ss.close();
				ta.append(" [ ������ ����Ǿ����ϴ�. ]" + "\n");
				bOn.setEnabled(true);
				bOff.setEnabled(false);
			} catch (IOException e1) {}
		}
	}
// �ι����� ����
	public void peopleAnswerList(){
		try{
			String str2="";
			FileReader fr = new FileReader("textFile\\PeopleQ.txt");
			BufferedReader br = new BufferedReader(fr);
			while((str2=br.readLine()) != null){
				vAPeopleQ.add(str2);
			}
		}catch(FileNotFoundException fi){
			JOptionPane.showMessageDialog(null, "PeopleQ.txt �� ã�� �� �����ϴ�.", "ERROR", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}catch(IOException ie){}
	}
//�������� ����
	public void musicAnswerList(){
		try{
			String str2="";
			FileReader fr = new FileReader("textFile\\MusicQ.txt");
			BufferedReader br = new BufferedReader(fr);
			while((str2=br.readLine()) != null){
				vAMusicQ.add(str2);
			}
		}catch(FileNotFoundException fi){
			JOptionPane.showMessageDialog(null, "MusicQ.txt �� ã�� �� �����ϴ�.", "ERROR", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}catch(IOException ie){}
	}
	public static void main(String[] args) {
		new QServertest();
	}
}
