package cn.itcast.pager;

/**
 * 它是SQL语句中的一个条件
 * 
 * @author qdmmy6
 * 
 */
public class Expression {
	public Expression() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Expression(String name, String operator, Object value) {
		super();
		this.name = name;
		this.operator = operator;
		this.value = value;
	}

	public Expression(String name, String operator) {
		super();
		this.name = name;
		this.operator = operator;
	}

	private String name;// 条件名
	private String operator;// 运算符，=、!=、<、> .. ，　like，is null
	private Object value;// 值

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
