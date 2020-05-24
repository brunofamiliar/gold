package br.com.gold.utils;

import java.util.ArrayList;
import java.util.List;

public class RuleEngine {
	private static List<Rule> rules = new ArrayList<>();
	private Rule rule = null;
	private int indexList = 0;
	
	private Operation operation, defaultOperation;
	private Expression expression;
	private List<Expression> expressions;
	private List<Operation> operations;
	
	private RuleEngine(RuleEngineBuilder builder) {
		this.expressions = builder.expressions;
		this.operations = builder.operations;
		this.expression = builder.expression;
		this.operation = builder.operation;
		this.defaultOperation = builder.defaultOperation;
	}
	
	public static class RuleEngineBuilder{
		private Operation operation, defaultOperation;
		private Expression expression;
		private List<Expression> expressions;
		private List<Operation> operations;
		
		public RuleEngineBuilder withExpressionsAndOperations(List<Expression> expressions, List<Operation> operations) {
			this.expressions = expressions;
			this.operations = operations;
			return this;
		}
		
		public RuleEngineBuilder withExpressionAndOperations(Expression expression, List<Operation> operations) {
			this.expression = expression;
			this.operations = operations;
			return this;
		}
		
		public RuleEngineBuilder withExpressionsAndOperation(List<Expression> expressions, Operation operation) {
			this.expressions = expressions;
			this.operation = operation;
			return this;
		}
		
		public RuleEngineBuilder withExpressionAndOperation(Expression expression, Operation operation) {
			this.expression = expression;
			this.operation = operation;
			return this;
		}
		
		public RuleEngineBuilder defaultOperation(Operation defaultOperation) {
			this.defaultOperation = defaultOperation;
			return this;
		}
		
		public RuleEngine build() {
			return new RuleEngine(this);
		}
	}
	
	static {
		rules.add(new ConditionResultRule());
	}
	
	public RuleEngine process() {
			
		if(expressions != null) {
			if(this.expressions.size() <= indexList)
				return this;
			
			expression = this.expressions.get(indexList);
		}
			
		
        	rule = rules.stream()
            .filter(r -> r.evaluate(expression))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Não existe regra para avaliar essa condição"));
        
        return this;
    }

	
	public Operation action() {
		if(expressions != null) {
			while(indexList < expressions.size()) {
				
				if(rule.getResult().result) {
					if (operations != null) {
						operations.get(indexList).execute();
						return operations.get(indexList);
					}else {
						operation.execute();
						return operation;	
					}
				}	
				
		    	indexList++;
	
		    	process();
			}
		}

		if(!rule.getResult().result) {
			defaultOperation.execute();
			return defaultOperation;
		}
		if (operations != null && indexList < operations.size()) {
			operations.get(indexList).execute();
			return operations.get(indexList);
		}
				
		operation.execute();
		return operation;

	}
	
}
