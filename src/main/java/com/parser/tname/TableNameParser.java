package com.parser.tname;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TableNameParser {

	private static final String TOKEN_COMMA = ",";
	private static final String KEYWORD_JOIN = "join";
	private static final String KEYWORD_INTO = "into";
	private static final String KEYWORD_TABLE = "table";
	private static final String KEYWORD_FROM = "from";
	
	private List<String> concerned = Arrays.asList(KEYWORD_TABLE, KEYWORD_INTO, KEYWORD_JOIN);
	private Set<String> tables = new HashSet<String>();

	public TableNameParser(final String sql) {
		String normalized = normalized(sql);
		String[] tokens = normalized.split("\\s+");
		int index = 0;
		while (moreTokens(tokens, index)) {
			String currentToken = tokens[index++];
			if(KEYWORD_FROM.equals(currentToken.toLowerCase())) {
				processFromToken(tokens, index);
			}
			else if (concerned.contains(currentToken.toLowerCase())) {	
				String nextToken = tokens[index++];			
				if(!nextToken.equals("(")) {					
					tables.add(nextToken);
				}

				if (moreTokens(tokens, index)) {					
					nextToken = tokens[index++];
				}
				while(nextToken.equals(TOKEN_COMMA) ) {
					nextToken = tokens[index++];
					tables.add(nextToken);
				}
			}
		}
	}

	private boolean moreTokens(final String[] tokens, int index) {
		return index < tokens.length;
	}

	private String normalized(final String sql) {
		String normalized = sql.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll(TOKEN_COMMA, " , ").replaceAll("\\(", " ( ").replaceAll("\\)", " ) ");
		if (normalized.endsWith(";")) {
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		return normalized;
	}

	private void processFromToken(final String[] split, int index) {
		String currentToken = split[index++];
		
		String nextToken = null;
		if (moreTokens(split, index)) {
			nextToken = split[index++];
		}
		this.tables.add(currentToken);
		if (nextToken != null && nextToken.equals(TOKEN_COMMA)) {
			while(nextToken.equals(TOKEN_COMMA)) {
				currentToken = split[index++];
				this.tables.add(currentToken);
				nextToken = split[index++];
			}
		} else {
			String nextNextToken = null;
			if (moreTokens(split, index)) {
				nextNextToken = split[index++];;
			}
			if (nextNextToken != null && nextNextToken.equals(TOKEN_COMMA)) {
				while(nextNextToken.equals(TOKEN_COMMA)) {
					if (moreTokens(split, index)) {						
						currentToken = split[index++];
					}
					if (moreTokens(split, index)) {
						nextToken = split[index++];
					}
					if (moreTokens(split, index)) {
						nextNextToken = split[index++];
					}
					this.tables.add(currentToken);
				}
			}			
		}		
	}

	public Collection<String> tables() {
		return new HashSet<String>(this.tables);
	}
	
}
