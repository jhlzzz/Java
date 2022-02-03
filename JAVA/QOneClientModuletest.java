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
			pick = dis.readUTF(); // 인물퀴즈인지 음악퀴즈인지 확인할때 사용
			
			if (pick.equals("@@인물퀴즈")) { // 인물퀴즈 선택했을 때 
				try {
					nick = dis.readUTF();  // 닉네임 체크
				} catch (IOException iePN) {
				}
				qs.peoplepPlayInfoName.add(nick);
				qs.peoplePlayInfoScore = new int[qs.vP.size()];
				qs.peoplePlayInfoScore[qs.vP.size() - 1] = score;
				qs.ta.append(nick + "님이 입장하셨습니다.(현재 접속자 수 : " + qs.vP.size() + ") \n");
				peopleBroadcast(nick + "님이 입장하셨습니다.(현재 접속자 수 : " + qs.vP.size() + ")");
				peopleSetPlayerInfo();
				try {
					while (true) {
						String msgPeople = dis.readUTF();
						if (msgPeople.equals("@@정원초과 or 게임진행중 검사"))
							continue; // 정원초과 or 게임진행중일때 더이상 유저가 입장못하게 함.
						if (msgPeople.equals("@@ready")) {
							qs.peopleReady++;
							peopleBroadcast(nick+"님 준비완료! ("+ qs.peopleReady+"/"+ qs.peoplepPlayInfoName.size()+")");
							String txt = "<게임 룰 설명>\n 1. 정답은 띄어쓰기 없이 한글로 작성하세요.\n 2. 3초 이내에 정답을 맞추세요.";
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
								new peopleGameStart().start(); // 3초후에 게임을 시작합니다.
							}
							continue;
						} else if (msgPeople.equals("@@gameend")) {
							qs.peopleReady = 0;
							qs.peopleGameStart = false;
							dos.writeUTF("게임이 종료됩니다."); // 객체당 하나씩 '게임이 종료됩니다'. 를 write 해준다. (broadcast하게되면 인원수대로 '게임이 종료됩니다'가 보내짐)
						// winner 체크
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
								dos.writeUTF("@@승리자" + maxIndexstr);
							}
						} else if (qs.peopleGameStart == true) { 
							int index = msgPeople.lastIndexOf(" ");
							String checkName = msgPeople.substring(index + 1);
							if (!qs.peopleAnswerExist) {
								if (checkName.equals(qs.peopleAnswer)) {
									qs.peopleAnswerExist = true;
									peopleBroadcast("@@정답");
									peopleBroadcast(nick + "님 정답!!    [" + qs.peopleAnswer + "]");
									for (int a = 0; a < qs.peoplepPlayInfoName.size(); a++) {
										if (qs.peoplepPlayInfoName.get(a) == nick) {
											score++;
											qs.peoplePlayInfoScore[a] = score;
										}
									}
									peopleSetPlayerInfo();
								} else {
									peopleBroadcast(nick + msgPeople); // 정답자가 없을때 채팅 broadcast
								}
							} else {
								peopleBroadcast(nick + msgPeople); // qs.gameStart == true 일때 채팅 broadcast
							}
						} else {
							peopleBroadcast(nick + msgPeople);
						}
					} 
				}catch (IOException ieP) {
					qs.vP.remove(this);
					closeAll();
				// 유저 퇴장시 왼쪽정렬
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
					peopleBroadcast(nick + "님이 퇴장하셨습니다.(현재 접속자 수 : " + qs.vP.size() + ")");
					qs.peopleReady=0;
					peopleSetPlayerInfo();
					if(qs.peopleGameStart == false) {
						peopleBroadcast("@@퇴장 레디초기화");
					}
					if(qs.vP.size() == 0) {
						qs.peopleGameStart = false;
						qs.peopleReady = 0;
					}
				}
			}
		// 음악퀴즈 선택했을때
			if (pick.equals("@@음악퀴즈")) {
				try {
					nick = dis.readUTF();
				} catch (IOException ieMN) {}
				qs.musicPlayInfoName.add(nick);
				qs.musicPlayInfoScore = new int[qs.vM.size()];
				qs.musicPlayInfoScore[qs.vM.size() - 1] = score;
				qs.ta.append(nick + "님이 입장하셨습니다.(현재 접속자 수 : " + qs.vM.size() + ") \n");
				musicBroadcast(nick + "님이 입장하셨습니다.(현재 접속자 수 : " + qs.vM.size() + ")");
				musicSetPlayerInfo();
				try {
					while (true) {
						String msgMusic = dis.readUTF();
						if (msgMusic.equals("@@정원초과 or 게임진행중 검사"))
							continue; // 정원초과 or 게임진행중일때 더이상 유저가 입장못하게 함.
						if (msgMusic.equals("@@Ready")) {
							qs.musicReady++;
							musicBroadcast(nick+"님 준비완료! ("+ qs.musicReady+"/"+ qs.musicPlayInfoName.size()+")");
							String txt = "<게임 룰 설명>\n 1. 정답은 띄어쓰기 없이 한글로 작성하세요.\n 2. 노래를 넘기시려면 패스 버튼을 누르세요.\n 3. 패스 단축키는 채팅창에 커서를 두시고,\n     [Tap + Space Bar] 입니다.\n";
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
								new musicGameStart().start(); // 3초후에 게임을 시작합니다.
							}
							continue;
						} else if (msgMusic.equals("@@gameend")) {
							qs.musicReady = 0;
							qs.musicGameStart = false;
							dos.writeUTF("게임이 종료됩니다."); // 객체당 하나씩 '게임이 종료됩니다'. 를 write 해준다. (broadcast하게되면 인원수대로 '게임이 종료됩니다'가 보내짐)
						// winner 체크
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
								dos.writeUTF("@@승리자" + maxIndexstr);
							}
						} else if (qs.musicGameStart == true) {
							if (msgMusic.equals("@@스킵")) {
								qs.skipNum++;
								musicBroadcast("스킵인원수 :" + qs.skipNum+"/"+qs.musicPlayInfoName.size());
								if (qs.skipNum == qs.vM.size()) {
									musicBroadcast("@@스킵");
								}
								continue;
							}
							int index = msgMusic.lastIndexOf(" ");
							String checkName = msgMusic.substring(index + 1);
							if (!qs.musicAnswerExist) {
								if (checkName.equals(qs.musicAnswer)) {
									qs.musicAnswerExist = true;
									musicBroadcast("@@정답");
									musicBroadcast(nick + "님 정답!!    [" + qs.musicAnswer + "]");
									for (int a = 0; a < qs.musicPlayInfoName.size(); a++) {
										if (qs.musicPlayInfoName.get(a) == nick) {
											score++;
											qs.musicPlayInfoScore[a] = score;
										}
									}
									musicSetPlayerInfo();
								} else {
									musicBroadcast(nick + msgMusic); // 정답자가 없을 때 채팅 broadcast
								}

							} else {
								musicBroadcast(nick + msgMusic); // qs.gameStart == true 일때 채팅 broadcast
							}
						} else {
							musicBroadcast(nick + msgMusic);
						}
					} 
				}catch (IOException ieM) {
					qs.vM.remove(this);
					closeAll();
				// 유저 퇴장시 왼쪽정렬
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
						musicBroadcast("@@퇴장");
					}
					if(qs.musicGameStart == false) {
						musicBroadcast("@@퇴장 레디초기화");
					}
					musicBroadcast(nick + "님이 퇴장하셨습니다.(현재 접속자 수 : " + qs.vM.size() + ")");
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
// 인물퀴즈 인포 셋팅
	public void peopleSetPlayerInfo() {
		for (int i = 0; i < qs.peoplepPlayInfoName.size(); i++) {
			String score = Integer.toString(qs.peoplePlayInfoScore[i]);
			String index = Integer.toString(i);
			peopleBroadcast("@@업데이트" + qs.peoplepPlayInfoName.get(i) + ":" + score + "#" + index);
		}
	}
// 음악퀴즈 인포 셋팅
	public void musicSetPlayerInfo() {
		for (int i = 0; i < qs.musicPlayInfoName.size(); i++) {
			String score = Integer.toString(qs.musicPlayInfoScore[i]);
			String index = Integer.toString(i);
			musicBroadcast("@@업데이트" + qs.musicPlayInfoName.get(i) + ":" + score + "#" + index);
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
// 인물퀴즈 랜덤셋팅
	public int peopleRandom() {
		Random r = new Random(); // 랜덤 객체 생성
		int rq = r.nextInt(qs.vAPeopleQ.size());
		return rq;
	}
// 음악퀴즈 랜덤셋팅 
	public int musicRandom() {
		Random r = new Random(); // 랜덤 객체 생성
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
// 인물퀴즈 스타트
	public class peopleGameStart extends Thread {
		public void run() {
			for (int i = 3; i >= 1; i--) {
				peopleBroadcast("               ※"+i + "초 뒤에 게임이 시작됩니다.");
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
				peopleBroadcast("\n<정답>\n" + qs.peopleAnswer + "\n");
				peopleBroadcast("※남은 문제 수 : " + remain);
			}
		}
	}// 인물퀴즈 스타트 내부클래스 끝
// 음악퀴즈 스타트
	public class musicGameStart extends Thread {
		public void run() {
			for (int i = 3; i >= 1; i--) {
				musicBroadcast("               ※"+i + "초 뒤에 게임이 시작됩니다.");
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
				musicBroadcast("\n<정답>\n" + qs.musicAnswer + "\n");
				musicBroadcast("※남은 문제 수 : " + remain);
			}
		} // 음악퀴즈 스타트 내부클래스 끝
	} // musicGameStart 끝
} // 모듈클래스 끝