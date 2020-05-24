package br.com.gold.utils;

public class ConditionResultRule implements Rule {
	private boolean result = false;
	
	@Override
	public boolean evaluate(Expression expression) {
		result = expression.getResult();
		return true;
	}
	
	 @Override
    public Result getResult() {
        return new Result(result);
    }
}
