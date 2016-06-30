package com.parser.tname;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TableNameParser {

	private static final String SPACE = " ";
	private static final String REGEX_SPACE = "\\s+";

	private static final String TOKEN_SEMI_COLON = ";";
	private static final String TOKEN_PARAN_START = "(";
	private static final String TOKEN_COMMA = ",";
	private static final String TOKEN_SET = "set";
	private static final String TOKEN_OF = "of";

	private static final String KEYWORD_JOIN = "join";
	private static final String KEYWORD_INTO = "into";
	private static final String KEYWORD_TABLE = "table";
	private static final String KEYWORD_FROM = "from";
	private static final String KEYWORD_USING = "using";
	private static final String KEYWORD_UPDATE = "update";

	private List<String> concerned = Arrays.asList(KEYWORD_TABLE, KEYWORD_INTO, KEYWORD_JOIN, KEYWORD_USING, KEYWORD_UPDATE);
	private List<String> ignored = Arrays.asList(TOKEN_PARAN_START, TOKEN_SET, TOKEN_OF);

	private Set<String> tables = new HashSet<String>();

	public TableNameParser(final String sql) {
		String normalized = normalized(sql);
		String[] tokens = normalized.split(REGEX_SPACE);
		int index = 0;
		while (moreTokens(tokens, index)) {
			String currentToken = tokens[index++];

			if(isFromToken(currentToken)) {
				processFromToken(tokens, index);
			}
			else if (shouldProcess(currentToken)) {	
				String nextToken = tokens[index++];	
				considerInclusion(nextToken);

				if (moreTokens(tokens, index)) {
					nextToken = tokens[index++];
				}
				/*while(nextToken.equals(TOKEN_COMMA)) {
					nextToken = tokens[index++];
					considerInclusion(nextToken);
				}*/
			}
		}
	}

	private boolean shouldProcess(String currentToken) {
		return concerned.contains(currentToken.toLowerCase());
	}

	private boolean isFromToken(String currentToken) {
		return KEYWORD_FROM.equals(currentToken.toLowerCase());
	}

	private String normalized(final String sql) {
		String normalized = sql.replaceAll("\\r\\n|\\r|\\n", SPACE).replaceAll(TOKEN_COMMA, " , ").replaceAll("\\(", " ( ").replaceAll("\\)", " ) ");
		if (normalized.endsWith(TOKEN_SEMI_COLON)) {
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		return normalized;
	}

	private void processFromToken(final String[] tokens, int index) {
		String currentToken = tokens[index++];
		considerInclusion(currentToken);
		
		String nextToken = null;
		if (moreTokens(tokens, index)) {
			nextToken = tokens[index++];
		}

		if (shouldProcessMultipleTables(nextToken)) {
			processNonAliasedMultiTables(tokens, index, nextToken);
		} else {
			processAliasedMultiTables(tokens, index, currentToken);
		}		
	}

	private void processNonAliasedMultiTables(final String[] tokens, int index, String nextToken) {
		while(nextToken.equals(TOKEN_COMMA)) {
			String currentToken = tokens[index++];
			considerInclusion(currentToken);
			nextToken = tokens[index++];
		}
	}

	private void processAliasedMultiTables(final String[] tokens, int index, String currentToken) {
		String nextNextToken = null;
		if (moreTokens(tokens, index)) {
			nextNextToken = tokens[index++];;
		}
		if (shouldProcessMultipleTables(nextNextToken)) {
			while(nextNextToken.equals(TOKEN_COMMA)) {
				if (moreTokens(tokens, index)) {
					currentToken = tokens[index++];
				}
				if (moreTokens(tokens, index)) {
					index++;
				}
				if (moreTokens(tokens, index)) {
					nextNextToken = tokens[index++];
				}
				considerInclusion(currentToken);
			}
		}
	}

	private boolean shouldProcessMultipleTables(final String nextToken) {
		return nextToken != null && nextToken.equals(TOKEN_COMMA);
	}

	private boolean moreTokens(final String[] tokens, int index) {
		return index < tokens.length;
	}

	private void considerInclusion(final String token) {
		if(!ignored.contains(token.toLowerCase())) {
			this.tables.add(token.toLowerCase());
		}
	}

	public Collection<String> tables() {
		return new HashSet<String>(this.tables);
	}
	
}
