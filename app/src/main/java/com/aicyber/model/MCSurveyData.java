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
		private String roundId = ""; // 答题次数
		private String questionId = "";// 问题ID
		private String problem = ""; // 问题内容
		private String flg = ""; // 标志
		private List<SurevyDataList> dataList = null; // 数据列

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
			private String Q = ""; // 问题
			private String A = ""; // 答案
			private String reminder = ""; // 提示语

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
