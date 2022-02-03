package test.teamprojecttest;

import java.net.*;
import java.util.*;
import java.io.*;

public class QOneClientModuletest extends Thread {
	QServertest qs;
	Socket s;
	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;
	String nick = "";
	int[] random = new int[10];
	String pick;
	int score;

	public QOneClientModuletest(QServertest qs) {
		this.qs = qs;
		s = qs.s;
		try {
			is = s.getInputStream();
			os = s.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
		} catch (IOException e) {
		}
	}
	@Override
	public void run() {
		listen();
	}
	public void listen() {
		try {
			pick = dis.readUTF(); // �ι��������� ������������ Ȯ���Ҷ� ���
			
			if (pick.equals("@@�ι�����")) { // �ι����� �������� �� 
				try {
					nick = dis.readUTF();  // �г��� üũ
				} catch (IOException iePN) {
				}
				qs.peoplepPlayInfoName.add(nick);
				qs.peoplePlayInfoScore = new int[qs.vP.size()];
				qs.peoplePlayInfoScore[qs.vP.size() - 1] = score;
				qs.ta.append(nick + "���� �����ϼ̽��ϴ�.(���� ������ �� : " + qs.vP.size() + ") \n");
				peopleBroadcast(nick + "���� �����ϼ̽��ϴ�.(���� ������ �� : " + qs.vP.size() + ")");
				peopleSetPlayerInfo();
				try {
					while (true) {
						String msgPeople = dis.readUTF();
						if (msgPeople.equals("@@�����ʰ� or ���������� �˻�"))
							continue; // �����ʰ� or �����������϶� ���̻� ������ ������ϰ� ��.
						if (msgPeople.equals("@@ready")) {
							qs.peopleReady++;
							peopleBroadcast(nick+"�� �غ�Ϸ�! ("+ qs.peopleReady+"/"+ qs.peoplepPlayInfoName.size()+")");
							String txt = "<���� �� ����>\n 1. ������ ���� ���� �ѱ۷� �ۼ��ϼ���.\n 2. 3�� �̳��� ������ ���߼���.";
				            dos.writeUTF(txt);

							for (int a = 0; a < qs.peoplepPlayInfoName.size(); a++) {
								if (qs.peoplepPlayInfoName.get(a) == nick) {
									score = 0;
									qs.peoplePlayInfoScore[a] = score;
								}
							}
							peopleSetPlayerInfo();
							if (qs.peopleReady == qs.peoplepPlayInfoName.size()) {
								qs.peopleGameStart = true;
								peopleBroadcast("@@randomset");
								for (int q = 0; q < 10; q++) {
									int rq = peopleRandom();
									random[q] = rq;
									String rqStr = Integer.toString(rq);
									peopleBroadcast(rqStr);
								}
								new peopleGameStart().start(); // 3���Ŀ� ������ �����մϴ�.
							}
							continue;
						} else if (msgPeople.equals("@@gameend")) {
							qs.peopleReady = 0;
							qs.peopleGameStart = false;
							dos.writeUTF("������ ����˴ϴ�."); // ��ü�� �ϳ��� '������ ����˴ϴ�'. �� write ���ش�. (broadcast�ϰԵǸ� �ο������ '������ ����˴ϴ�'�� ������)
						// winner üũ
							int max = 0;
							int maxIndex = 0;
							String maxIndexstr = "";
							int equalsIndex = 0;
							
							for (int i = 1; i < qs.peoplePlayInfoScore.length; i++) {
								if (qs.peoplePlayInfoScore[i] > qs.peoplePlayInfoScore[maxIndex]) {
									maxIndex = i;
								}
							}
							for (int j = 0; j < qs.peoplePlayInfoScore.length; j++) {
								if (qs.peoplePlayInfoScore[j] == qs.peoplePlayInfoScore[maxIndex]) {
									equalsIndex++;
								}
							}
							int maxIndex2 = 0;
							int[] equals = new int[equalsIndex];
							for (int e = 0; e < equalsIndex; e++) {
								for (int k = maxIndex2; k < qs.peoplePlayInfoScore.length; k++) {
									if (k == maxIndex2) {
										continue;
									} else if (qs.peoplePlayInfoScore[k] == qs.peoplePlayInfoScore[maxIndex]) {
										equals[e] = k;
										maxIndex2 = k;
										break;
									}
								}
							}
							for (int wi = 0; wi < equals.length; wi++) {
								maxIndexstr = Integer.toString(equals[wi]);
								dos.writeUTF("@@�¸���" + maxIndexstr);
							}
						} else if (qs.peopleGameStart == true) { 
							int index = msgPeople.lastIndexOf(" ");
							String checkName = msgPeople.substring(index + 1);
							if (!qs.peopleAnswerExist) {
								if (checkName.equals(qs.peopleAnswer)) {
									qs.peopleAnswerExist = true;
									peopleBroadcast("@@����");
									peopleBroadcast(nick + "�� ����!!    [" + qs.peopleAnswer + "]");
									for (int a = 0; a < qs.peoplepPlayInfoName.size(); a++) {
										if (qs.peoplepPlayInfoName.get(a) == nick) {
											score++;
											qs.peoplePlayInfoScore[a] = score;
										}
									}
									peopleSetPlayerInfo();
								} else {
									peopleBroadcast(nick + msgPeople); // �����ڰ� ������ ä�� broadcast
								}
							} else {
								peopleBroadcast(nick + msgPeople); // qs.gameStart == true �϶� ä�� broadcast
							}
						} else {
							peopleBroadcast(nick + msgPeople);
						}
					} 
				}catch (IOException ieP) {
					qs.vP.remove(this);
					closeAll();
				// ���� ����� ��������
					for (int nickIndex = 0; nickIndex < qs.peoplepPlayInfoName.size(); nickIndex++) {
						if (nick.equals(qs.peoplepPlayInfoName.get(nickIndex))) {
							qs.peoplepPlayInfoName.remove(nickIndex);
							for (int j = nickIndex; j < qs.peoplePlayInfoScore.length - 1; j++) {
								qs.peoplePlayInfoScore[j] = qs.peoplePlayInfoScore[j + 1];
							}
							break;
						}
					}
					qs.peoplePlayInfoScore = new int[qs.peoplepPlayInfoName.size()];
					peopleBroadcast(nick + "���� �����ϼ̽��ϴ�.(���� ������ �� : " + qs.vP.size() + ")");
					qs.peopleReady=0;
					peopleSetPlayerInfo();
					if(qs.peopleGameStart == false) {
						peopleBroadcast("@@���� �����ʱ�ȭ");
					}
					if(qs.vP.size() == 0) {
						qs.peopleGameStart = false;
						qs.peopleReady = 0;
					}
				}
			}
		// �������� ����������
			if (pick.equals("@@��������")) {
				try {
					nick = dis.readUTF();
				} catch (IOException ieMN) {}
				qs.musicPlayInfoName.add(nick);
				qs.musicPlayInfoScore = new int[qs.vM.size()];
				qs.musicPlayInfoScore[qs.vM.size() - 1] = score;
				qs.ta.append(nick + "���� �����ϼ̽��ϴ�.(���� ������ �� : " + qs.vM.size() + ") \n");
				musicBroadcast(nick + "���� �����ϼ̽��ϴ�.(���� ������ �� : " + qs.vM.size() + ")");
				musicSetPlayerInfo();
				try {
					while (true) {
						String msgMusic = dis.readUTF();
						if (msgMusic.equals("@@�����ʰ� or ���������� �˻�"))
							continue; // �����ʰ� or �����������϶� ���̻� ������ ������ϰ� ��.
						if (msgMusic.equals("@@Ready")) {
							qs.musicReady++;
							musicBroadcast(nick+"�� �غ�Ϸ�! ("+ qs.musicReady+"/"+ qs.musicPlayInfoName.size()+")");
							String txt = "<���� �� ����>\n 1. ������ ���� ���� �ѱ۷� �ۼ��ϼ���.\n 2. �뷡�� �ѱ�÷��� �н� ��ư�� ��������.\n 3. �н� ����Ű�� ä��â�� Ŀ���� �νð�,\n     [Tap + Space Bar] �Դϴ�.\n";
				            dos.writeUTF(txt);

							for (int a = 0; a < qs.musicPlayInfoName.size(); a++) {
								if (qs.musicPlayInfoName.get(a) == nick) {
									score = 0;
									qs.musicPlayInfoScore[a] = score;
								}
							}
							musicSetPlayerInfo();
							if (qs.musicReady == qs.musicPlayInfoName.size()) {
								qs.musicGameStart = true;
								musicBroadcast("@@randomset");
								for (int q = 0; q < 10; q++) {
									int rq = musicRandom();
									random[q] = rq;
									String rqStr = Integer.toString(rq);
									musicBroadcast(rqStr);
								}
								new musicGameStart().start(); // 3���Ŀ� ������ �����մϴ�.
							}
							continue;
						} else if (msgMusic.equals("@@gameend")) {
							qs.musicReady = 0;
							qs.musicGameStart = false;
							dos.writeUTF("������ ����˴ϴ�."); // ��ü�� �ϳ��� '������ ����˴ϴ�'. �� write ���ش�. (broadcast�ϰԵǸ� �ο������ '������ ����˴ϴ�'�� ������)
						// winner üũ
							int max = 0;
							int maxIndex = 0;
							String maxIndexstr = "";
							int equalsIndex = 0;
							for (int i = 1; i < qs.musicPlayInfoScore.length; i++) {
								if (qs.musicPlayInfoScore[i] > qs.musicPlayInfoScore[maxIndex]) {
									maxIndex = i;
								}
							}
							for (int j = 0; j < qs.musicPlayInfoScore.length; j++) {
								if (qs.musicPlayInfoScore[j] == qs.musicPlayInfoScore[maxIndex]) {
									equalsIndex++;
								}
							}
							int maxIndex2 = 0;
							int[] equals = new int[equalsIndex];
							for (int e = 0; e < equalsIndex; e++) {
								for (int k = maxIndex2; k < qs.musicPlayInfoScore.length; k++) {
									if (k == maxIndex2) {
										continue;
									} else if (qs.musicPlayInfoScore[k] == qs.musicPlayInfoScore[maxIndex]) {
										equals[e] = k;
										maxIndex2 = k;
										break;
									}
								}
							}
							for (int wi = 0; wi < equals.length; wi++) {
								maxIndexstr = Integer.toString(equals[wi]);
								dos.writeUTF("@@�¸���" + maxIndexstr);
							}
						} else if (qs.musicGameStart == true) {
							if (msgMusic.equals("@@��ŵ")) {
								qs.skipNum++;
								musicBroadcast("��ŵ�ο��� :" + qs.skipNum+"/"+qs.musicPlayInfoName.size());
								if (qs.skipNum == qs.vM.size()) {
									musicBroadcast("@@��ŵ");
								}
								continue;
							}
							int index = msgMusic.lastIndexOf(" ");
							String checkName = msgMusic.substring(index + 1);
							if (!qs.musicAnswerExist) {
								if (checkName.equals(qs.musicAnswer)) {
									qs.musicAnswerExist = true;
									musicBroadcast("@@����");
									musicBroadcast(nick + "�� ����!!    [" + qs.musicAnswer + "]");
									for (int a = 0; a < qs.musicPlayInfoName.size(); a++) {
										if (qs.musicPlayInfoName.get(a) == nick) {
											score++;
											qs.musicPlayInfoScore[a] = score;
										}
									}
									musicSetPlayerInfo();
								} else {
									musicBroadcast(nick + msgMusic); // �����ڰ� ���� �� ä�� broadcast
								}

							} else {
								musicBroadcast(nick + msgMusic); // qs.gameStart == true �϶� ä�� broadcast
							}
						} else {
							musicBroadcast(nick + msgMusic);
						}
					} 
				}catch (IOException ieM) {
					qs.vM.remove(this);
					closeAll();
				// ���� ����� ��������
					for (int nickIndex = 0; nickIndex < qs.musicPlayInfoName.size(); nickIndex++) {
						if (nick.equals(qs.musicPlayInfoName.get(nickIndex))) {
							qs.musicPlayInfoName.remove(nickIndex);
							for (int j = nickIndex; j < qs.musicPlayInfoScore.length - 1; j++) {
								qs.musicPlayInfoScore[j] = qs.musicPlayInfoScore[j + 1];
							}
							break;
						}
					}
					qs.musicPlayInfoScore = new int[qs.musicPlayInfoName.size()];
					if(qs.musicGameStart == true) {
						musicBroadcast("@@����");
					}
					if(qs.musicGameStart == false) {
						musicBroadcast("@@���� �����ʱ�ȭ");
					}
					musicBroadcast(nick + "���� �����ϼ̽��ϴ�.(���� ������ �� : " + qs.vM.size() + ")");
					qs.musicReady=0;
					qs.skipNum = 0;
					musicSetPlayerInfo();
					if(qs.vM.size() == 0) {
						qs.musicGameStart = false;
						qs.musicReady = 0;
						qs.skipNum = 0;
					}
				}
			}
		} catch (IOException ieMain) {
		}
	}
// �ι����� ���� ����
	public void peopleSetPlayerInfo() {
		for (int i = 0; i < qs.peoplepPlayInfoName.size(); i++) {
			String score = Integer.toString(qs.peoplePlayInfoScore[i]);
			String index = Integer.toString(i);
			peopleBroadcast("@@������Ʈ" + qs.peoplepPlayInfoName.get(i) + ":" + score + "#" + index);
		}
	}
// �������� ���� ����
	public void musicSetPlayerInfo() {
		for (int i = 0; i < qs.musicPlayInfoName.size(); i++) {
			String score = Integer.toString(qs.musicPlayInfoScore[i]);
			String index = Integer.toString(i);
			musicBroadcast("@@������Ʈ" + qs.musicPlayInfoName.get(i) + ":" + score + "#" + index);
		}
	}
	public void peopleBroadcast(String msg) {
		try {
			for (QOneClientModuletest qocm : qs.vP) {
				qocm.dos.writeUTF(msg);
				qocm.dos.flush();
			}
		} catch (IOException e) {
		}
	}
	public void musicBroadcast(String msg) {
		try {
			for (QOneClientModuletest qocm : qs.vM) {
				qocm.dos.writeUTF(msg);
				qocm.dos.flush();
			}
		} catch (IOException e) {
		}
	}
// �ι����� ��������
	public int peopleRandom() {
		Random r = new Random(); // ���� ��ü ����
		int rq = r.nextInt(qs.vAPeopleQ.size());
		return rq;
	}
// �������� �������� 
	public int musicRandom() {
		Random r = new Random(); // ���� ��ü ����
		int rq = r.nextInt(qs.vAMusicQ.size());
		return rq;
	}
	public void closeAll() {
		try {
			if (dis != null)
				dis.close();
			if (dos != null)
				dos.close();
			if (is != null)
				is.close();
			if (os != null)
				os.close();
			if (s != null)
				s.close();
		} catch (IOException ie) {
		}
	}
// �ι����� ��ŸƮ
	public class peopleGameStart extends Thread {
		public void run() {
			for (int i = 3; i >= 1; i--) {
				peopleBroadcast("               ��"+i + "�� �ڿ� ������ ���۵˴ϴ�.");
				try {
					sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			peopleBroadcast("@@gamestart");
			int remain = 10;
			for (int q = 0; q < 10; q++) {
				if(qs.vP.size() == 0) {
					break;
				}
				qs.peopleAnswerExist = false;
				int rd = random[q];
				qs.peopleAnswer = qs.vAPeopleQ.get(rd);
				
				try {
					sleep(3000);
				} catch (InterruptedException e) {
				}
				remain--;
				peopleBroadcast("\n<����>\n" + qs.peopleAnswer + "\n");
				peopleBroadcast("�س��� ���� �� : " + remain);
			}
		}
	}// �ι����� ��ŸƮ ����Ŭ���� ��
// �������� ��ŸƮ
	public class musicGameStart extends Thread {
		public void run() {
			for (int i = 3; i >= 1; i--) {
				musicBroadcast("               ��"+i + "�� �ڿ� ������ ���۵˴ϴ�.");
				try {
					sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			musicBroadcast("@@gamestart");
			int remain = 10;
			for (int q = 0; q < 10; q++) {
				if(qs.vM.size() == 0) {
					break;
				}
				qs.skipNum = 0;
				qs.musicAnswerExist = false;
				int rd = random[q];
				qs.musicAnswer = qs.vAMusicQ.get(rd);
				
				while (true) {
					if (qs.skipNum == qs.vM.size()) {
						break;
					}
					try {
						sleep(100);
					} catch (InterruptedException e) {
					}
				}
				remain--;
				musicBroadcast("\n<����>\n" + qs.musicAnswer + "\n");
				musicBroadcast("�س��� ���� �� : " + remain);
			}
		} // �������� ��ŸƮ ����Ŭ���� ��
	} // musicGameStart ��
} // ���Ŭ���� ��