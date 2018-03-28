package com.aicyber.model;

import java.util.List;

public class MCSurveyData extends MFunctionData {

	private Databody data = null;

	public Databody getData() {
		return data;
	}

	public void setData(Databody data) {
		this.data = data;
	}

	public class Databody {
		private String roundId = ""; // �������
		private String questionId = "";// ����ID
		private String problem = ""; // ��������
		private String flg = ""; // ��־
		private List<SurevyDataList> dataList = null; // ������

		public List<SurevyDataList> getDataList() {
			return dataList;
		}

		public void setDataList(List<SurevyDataList> dataList) {
			this.dataList = dataList;
		}

		public String getRoundId() {
			return roundId;
		}

		public void setRoundId(String roundId) {
			this.roundId = roundId;
		}

		public String getQuestionId() {
			return questionId;
		}

		public void setQuestionId(String questionId) {
			this.questionId = questionId;
		}

		public String getProblem() {
			return problem;
		}

		public void setProblem(String problem) {
			this.problem = problem;
		}

		public String getFlg() {
			return flg;
		}

		public void setFlg(String flg) {
			this.flg = flg;
		}

		public class SurevyDataList {
			private String Q = ""; // ����
			private String A = ""; // ��
			private String reminder = ""; // ��ʾ��

			public String getReminder() {
				return reminder;
			}

			public void setReminder(String reminder) {
				this.reminder = reminder;
			}

			public String getQ() {
				return Q;
			}

			public void setQ(String q) {
				Q = q;
			}

			public String getA() {
				return A;
			}

			public void setA(String a) {
				A = a;
			}
		}
	}
}
