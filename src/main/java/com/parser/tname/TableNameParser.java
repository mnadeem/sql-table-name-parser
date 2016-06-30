package com.parser.tname;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TableNameParser {

	private static final String TOKEN_SEMI_COLON = ";";
	private static final String TOKEN_PARAN_START = "(";
	private static final String TOKEN_COMMA = ",";
	private static final String KEYWORD_JOIN = "join";
	private static final String KEYWORD_INTO = "into";
	private static final String KEYWORD_TABLE = "table";
	private static final String KEYWORD_FROM = "from";
	private static final String KEYWORD_USING = "using";
	
	private List<String> concerned = Arrays.asList(KEYWORD_TABLE, KEYWORD_INTO, KEYWORD_JOIN, KEYWORD_USING);
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
				considerInclusion(nextToken);			

				if (moreTokens(tokens, index)) {					
					nextToken = tokens[index++];
				}
				while(nextToken.equals(TOKEN_COMMA) ) {
					nextToken = tokens[index++];
					considerInclusion(nextToken);
				}
			}
		}
	}

	private boolean moreTokens(final String[] tokens, int index) {
		return index < tokens.length;
	}

	private String normalized(final String sql) {
		String normalized = sql.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll(TOKEN_COMMA, " , ").replaceAll("\\(", " ( ").replaceAll("\\)", " ) ");
		if (normalized.endsWith(TOKEN_SEMI_COLON)) {
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		return normalized;
	}

	private void processFromToken(final String[] tokens, int index) {
		String currentToken = tokens[index++];
		
		String nextToken = null;
		if (moreTokens(tokens, index)) {
			nextToken = tokens[index++];
		}
		considerInclusion(currentToken);
		if (nextToken != null && nextToken.equals(TOKEN_COMMA)) {
			while(nextToken.equals(TOKEN_COMMA)) {
				currentToken = tokens[index++];
				considerInclusion(currentToken);
				nextToken = tokens[index++];
			}
		} else {
			String nextNextToken = null;
			if (moreTokens(tokens, index)) {
				nextNextToken = tokens[index++];;
			}
			if (nextNextToken != null && nextNextToken.equals(TOKEN_COMMA)) {
				while(nextNextToken.equals(TOKEN_COMMA)) {
					if (moreTokens(tokens, index)) {						
						currentToken = tokens[index++];
					}
					if (moreTokens(tokens, index)) {
						nextToken = tokens[index++];
					}
					if (moreTokens(tokens, index)) {
						nextNextToken = tokens[index++];
					}
					considerInclusion(currentToken);
				}
			}			
		}		
	}

	private void considerInclusion(String token) {
		if(!token.equals(TOKEN_PARAN_START)) {					
			this.tables.add(token.toLowerCase());
		}		
	}

	public Collection<String> tables() {
		return new HashSet<String>(this.tables);
	}
	
}
