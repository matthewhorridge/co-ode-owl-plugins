package org.coode.parsers.oppl.lint;

// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g 2010-07-29 13:17:27
import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

public class OPPLLintLexer extends Lexer {
	public static final int VALUE_RESTRICTION = 63;
	public static final int LETTER = 43;
	public static final int REMOVE = 91;
	public static final int TYPES = 39;
	public static final int SAME_AS_AXIOM = 52;
	public static final int INVERSE_OF = 25;
	public static final int NOT = 12;
	public static final int SUBCLASS_OF = 20;
	public static final int EOF = -1;
	public static final int ESCLAMATION_MARK = 149;
	public static final int CREATE = 80;
	public static final int POW = 36;
	public static final int NOT_EQUAL = 72;
	public static final int INVERSE_OBJECT_PROPERTY_EXPRESSION = 68;
	public static final int INSTANCE_OF = 38;
	public static final int BEGIN = 83;
	public static final int RETURN = 415;
	public static final int QUESTION_MARK = 46;
	public static final int SYMMETRIC = 30;
	public static final int CARDINALITY_RESTRICTION = 64;
	public static final int SELECT = 75;
	public static final int ROLE_ASSERTION = 67;
	public static final int DIFFERENT_FROM_AXIOM = 53;
	public static final int TRANSITIVE = 34;
	public static final int ANTI_SYMMETRIC = 31;
	public static final int ALL_RESTRICTION = 62;
	public static final int CONJUNCTION = 56;
	public static final int NEGATED_ASSERTION = 59;
	public static final int WHITESPACE = 9;
	public static final int MATCH = 176;
	public static final int SEMICOLON = 422;
	public static final int VALUE = 18;
	public static final int FAIL = 529;
	public static final int GROUPS = 356;
	public static final int OPEN_CURLY_BRACES = 6;
	public static final int DISJUNCTION = 55;
	public static final int INVERSE = 19;
	public static final int DBLQUOTE = 40;
	public static final int OR = 11;
	public static final int CONSTANT = 70;
	public static final int ENTITY_REFERENCE = 45;
	public static final int END = 84;
	public static final int COMPOSITION = 4;
	public static final int CLOSED_SQUARE_BRACKET = 86;
	public static final int DOLLAR = 400;
	public static final int SAME_AS = 23;
	public static final int WHERE = 71;
	public static final int DISJOINT_WITH = 26;
	public static final int SUPER_PROPERTY_OF = 88;
	public static final int VARIABLE_TYPE = 89;
	public static final int CLOSED_PARENTHESYS = 8;
	public static final int ONLY = 14;
	public static final int EQUIVALENT_TO_AXIOM = 49;
	public static final int SUB_PROPERTY_OF = 21;
	public static final int NEGATED_EXPRESSION = 58;
	public static final int MAX = 16;
	public static final int CREATE_DISJUNCTION = 82;
	public static final int AND = 10;
	public static final int INVERSE_PROPERTY = 60;
	public static final int VARIABLE_NAME = 464;
	public static final int DIFFERENT_FROM = 24;
	public static final int IN = 74;
	public static final int EQUIVALENT_TO = 22;
	public static final int UNARY_AXIOM = 54;
	public static final int COMMA = 37;
	public static final int CLOSED_CURLY_BRACES = 7;
	public static final int IDENTIFIER = 44;
	public static final int SOME = 13;
	public static final int EQUAL = 73;
	public static final int OPEN_PARENTHESYS = 5;
	public static final int REFLEXIVE = 32;
	public static final int PLUS = 79;
	public static final int DIGIT = 41;
	public static final int DOT = 78;
	public static final int SUPER_CLASS_OF = 87;
	public static final int EXPRESSION = 69;
	public static final int SOME_RESTRICTION = 61;
	public static final int ADD = 90;
	public static final int INTEGER = 42;
	public static final int EXACTLY = 17;
	public static final int SUB_PROPERTY_AXIOM = 51;
	public static final int OPEN_SQUARE_BRACKET = 85;
	public static final int VALUES = 354;
	public static final int RANGE = 28;
	public static final int ONE_OF = 65;
	public static final int MIN = 15;
	public static final int SUB_CLASS_AXIOM = 48;
	public static final int Tokens = 47;
	public static final int DOMAIN = 27;
	public static final int SUBPROPERTY_OF = 105;
	public static final int COLON = 77;
	public static final int DISJOINT_WITH_AXIOM = 50;
	public static final int CREATE_INTERSECTION = 81;
	public static final int INVERSE_FUNCTIONAL = 35;
	public static final int RENDERING = 355;
	public static final int IRREFLEXIVE = 33;
	public static final int ASSERTED = 76;
	public static final int FUNCTIONAL = 29;
	public static final int PROPERTY_CHAIN = 57;
	public static final int TYPE_ASSERTION = 66;
	// delegates
	public OPPLLintLexer_OPPLLexer_MOWLLexer gMOWLLexer;
	public OPPLLintLexer_OPPLLexer gOPPLLexer;

	// delegators
	public OPPLLintLexer() {
		;
	}

	public OPPLLintLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public OPPLLintLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
		this.gOPPLLexer = new OPPLLintLexer_OPPLLexer(input, state, this);
	}

	@Override
	public String getGrammarFileName() {
		return "/Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g";
	}

	// $ANTLR start "DOLLAR"
	public final void mDOLLAR() throws RecognitionException {
		try {
			int _type = DOLLAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:12:3:
			// ( '$' )
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:13:5:
			// '$'
			{
				this.match('$');
			}
			this.state.type = _type;
			this.state.channel = _channel;
		} finally {
		}
	}

	// $ANTLR end "DOLLAR"
	// $ANTLR start "RETURN"
	public final void mRETURN() throws RecognitionException {
		try {
			int _type = RETURN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:17:3:
			// ( 'RETURN' )
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:18:5:
			// 'RETURN'
			{
				this.match("RETURN");
			}
			this.state.type = _type;
			this.state.channel = _channel;
		} finally {
		}
	}

	// $ANTLR end "RETURN"
	// $ANTLR start "SEMICOLON"
	public final void mSEMICOLON() throws RecognitionException {
		try {
			int _type = SEMICOLON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:22:2:
			// ( ';' )
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:23:3:
			// ';'
			{
				this.match(';');
			}
			this.state.type = _type;
			this.state.channel = _channel;
		} finally {
		}
	}

	// $ANTLR end "SEMICOLON"
	@Override
	public void mTokens() throws RecognitionException {
		// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:1:8: (
		// DOLLAR | RETURN | SEMICOLON | MOWLLexer. Tokens | OPPLLexer. Tokens )
		int alt1 = 5;
		alt1 = this.dfa1.predict(this.input);
		switch (alt1) {
		case 1:
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:1:10:
			// DOLLAR
		{
			this.mDOLLAR();
		}
			break;
		case 2:
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:1:17:
			// RETURN
		{
			this.mRETURN();
		}
			break;
		case 3:
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:1:24:
			// SEMICOLON
		{
			this.mSEMICOLON();
		}
			break;
		case 4:
		case 5:
			// /Users/luigi/Documents/workspace/Parsers/src/OPPLLintLexer.g:1:51:
			// OPPLLexer. Tokens
		{
			this.gOPPLLexer.mTokens();
		}
			break;
		}
	}

	protected DFA1 dfa1 = new DFA1(this);
	static final String DFA1_eotS = "\2\uffff\1\56\1\uffff\1\56\5\uffff\20\56\3\uffff\2\56\1\uffff\3"
			+ "\56\1\uffff\12\56\1\uffff\44\56\1\uffff\4\56\1\uffff\66\56\1\uffff"
			+ "\3\56\3\uffff\147\56\1\u0121\43\56\1\uffff\u00ab\56";
	static final String DFA1_eofS = "\u01cd\uffff";
	static final String DFA1_minS = "\1\11\1\uffff\1\55\1\uffff\1\55\5\uffff\20\55\2\uffff\1\0\1\60\1"
			+ "\55\1\101\3\55\1\uffff\12\55\1\uffff\44\55\1\0\1\42\3\55\1\47\66"
			+ "\55\1\0\3\55\1\47\1\uffff\1\47\u008b\55\1\uffff\u00ab\55";
	static final String DFA1_maxS = "\1\175\1\uffff\1\172\1\uffff\1\172\5\uffff\20\172\2\uffff\1\uffff"
			+ "\1\71\5\172\1\uffff\12\172\1\uffff\44\172\1\uffff\1\42\72\172\1"
			+ "\uffff\4\172\1\uffff\u008c\172\1\uffff\u00ab\172";
	static final String DFA1_acceptS = "\1\uffff\1\1\1\uffff\1\3\1\uffff\5\4\20\uffff\2\4\7\uffff\1\5\12"
			+ "\uffff\1\4\145\uffff\1\4\u008c\uffff\1\2\u00ab\uffff";
	static final String DFA1_specialS = "\34\uffff\1\2\66\uffff\1\0\73\uffff\1\1\u013d\uffff}>";
	static final String[] DFA1_transitionS = {
			"\2\11\2\uffff\1\11\22\uffff\1\11\1\43\1\34\1\uffff\1\1\2\uffff"
					+ "\1\37\1\5\1\10\1\uffff\1\43\1\33\1\uffff\1\43\1\uffff\12\35"
					+ "\1\43\1\3\1\uffff\1\43\1\uffff\1\43\1\uffff\1\30\1\44\1\36\1"
					+ "\24\1\45\1\27\1\51\1\52\1\21\3\52\1\46\1\14\1\40\2\52\1\2\1"
					+ "\22\1\31\1\52\1\47\1\41\3\52\1\43\1\uffff\1\43\1\32\2\uffff"
					+ "\1\12\1\52\1\42\1\23\1\17\1\50\2\52\1\25\3\52\1\16\1\13\1\4"
					+ "\2\52\1\26\1\15\1\31\1\52\1\20\4\52\1\6\1\uffff\1\7",
			"",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\53\25\57\4\uffff\1\57\1"
					+ "\uffff\1\54\3\57\1\55\25\57",
			"",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57"
					+ "\1\61\3\57\1\60\10\57",
			"",
			"",
			"",
			"",
			"",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\62\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\63\13\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\64\13\57\4\uffff\1\57\1"
					+ "\uffff\16\57\1\65\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\70"
					+ "\15\57\1\66\5\57\1\67\3\57\1\71\1\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\73" + "\7\57\1\72\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\20\57" + "\1\75\6\57\1\74\2\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\76" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\15\57\1\77\14\57\4\uffff\1\57\1"
					+ "\uffff\15\57\1\100\3\57\1\101\10\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\103\25\57\4\uffff\1\57\1"
					+ "\uffff\1\102\27\57\1\71\1\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57"
					+ "\1\104\5\57\1\105\13\57",
			"\1\57\2\uffff\13\57\6\uffff\1\110\31\57\4\uffff\1\57\1\uffff"
					+ "\10\57\1\106\5\57\1\107\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57"
					+ "\1\111\3\57\1\101\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\112" + "\3\57\1\55\25\57",
			"\1\57\2\uffff\13\57\6\uffff\1\114\31\57\4\uffff\1\57\1\uffff"
					+ "\1\115\23\57\1\113\5\57",
			"\1\57\2\uffff\13\57\6\uffff\3\57\1\117\16\57\1\120\7\57\4\uffff"
					+ "\1\57\1\uffff\15\57\1\116\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57"
					+ "\1\121\6\57\1\122\1\57",
			"",
			"",
			"\42\123\1\124\uffdd\123",
			"\12\35",
			"\1\57\2\uffff\13\57\6\uffff\13\57\1\125\2\57\1\126\13\57\4"
					+ "\uffff\1\57\1\uffff\21\57\1\127\10\57",
			"\32\130\6\uffff\32\130",
			"\1\57\2\uffff\13\57\6\uffff\1\57\1\131\30\57\4\uffff\1\57\1" + "\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\7\57\1\132\22\57\4\uffff\1\57\1" + "\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\133\10\57",
			"",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\134\25\57\4\uffff\1\57\1" + "\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\15\57\1\135\14\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\1\136\31\57\4\uffff\1\57\1\uffff" + "\1\137\31\57",
			"\1\57\2\uffff\13\57\6\uffff\1\140\31\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\141" + "\23\57\1\113\5\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\142\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\14\57\1\144\1\145\5\57\1\143\6"
					+ "\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\146\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\147\24\57",
			"",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\150\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\3\57"
					+ "\1\151\17\57\1\152\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\153\6\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\154\6\57\4\uffff\1\57\1" + "\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\155\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\156\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\57"
					+ "\1\157\15\57\1\160\12\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\161\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\162\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\163\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\27\57" + "\1\164\2\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\165" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\24\57" + "\1\166\5\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\167\16\57",
			"\1\57\2\uffff\13\57\6\uffff\3\57\1\171\21\57\1\170\4\57\4\uffff"
					+ "\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57"
					+ "\1\173\2\57\1\172\4\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\174\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\175\15\57",
			"\1\57\2\uffff\13\57\6\uffff\13\57\1\176\16\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57"
					+ "\1\177\14\57\1\u0080\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\u0081\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57"
					+ "\1\u0082\14\57\1\u0083\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\u0084\15\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u0085\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57"
					+ "\1\173\2\57\1\u0086\4\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0087\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0088\14\57",
			"\1\57\2\uffff\13\57\6\uffff\10\57\1\u0089\21\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u008a\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\152\6\57",
			"\1\57\2\uffff\13\57\6\uffff\3\57\1\u008b\26\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\22\57\1\u008c\7\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u008d" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\17\57" + "\1\u008e\12\57",
			"\42\123\1\124\uffdd\123",
			"\1\u008f",
			"\1\57\2\uffff\13\57\6\uffff\1\u0090\31\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\15\57\1\u0091\14\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0092\25\57",
			"\1\u0094\5\uffff\1\u0095\2\uffff\13\u0095\6\uffff\32\u0093"
					+ "\4\uffff\1\u0095\1\uffff\32\u0093",
			"\1\57\2\uffff\13\57\6\uffff\11\57\1\u0096\20\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u0097\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0098\25\57",
			"\1\57\2\uffff\13\57\6\uffff\6\57\1\u0099\23\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\3\57\1\u009a\26\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u009b\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u009c\6\57",
			"\1\57\2\uffff\13\57\6\uffff\13\57\1\u009d\16\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u009e\21\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u009f\13\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\24\57\1\u00a0\5\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u00a1\13\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\3\57\1\u00a2\26\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\6\57" + "\1\u00a3\23\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u00a4\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\30\57" + "\1\u00a5\1\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u00a6\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00a7\25\57",
			"\1\57\2\uffff\13\57\6\uffff\2\57\1\u00a8\14\57\1\u00a9\12\57"
					+ "\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00aa\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00ab\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\u00ac\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u00ad\27\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u00ae\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\24\57" + "\1\u00af\5\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\10\57\1\u00b0\21\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00b1\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u00b2\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00b3\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00b4\25\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u00b5\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u00b6\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\11\57" + "\1\u00b7\20\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u00b8" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u00b9\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\11\57" + "\1\u00ba\20\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u00bb" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\1\u00bc\31\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00bd\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\6\57" + "\1\u00be\23\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u00bf\27\57",
			"\1\57\2\uffff\13\57\6\uffff\13\57\1\u00c0\16\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u00c1\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u00c2\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u00c3\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00c4\25\57",
			"\42\123\1\124\uffdd\123",
			"\1\57\2\uffff\13\57\6\uffff\22\57\1\u00c5\7\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\22\57\1\u00c6\7\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u00c7" + "\31\57",
			"\1\u0094\5\uffff\1\u0095\2\uffff\13\u0095\6\uffff\32\u0093"
					+ "\4\uffff\1\u0095\1\uffff\32\u0093",
			"",
			"\1\u0094\5\uffff\1\u0095\2\uffff\13\u0095\6\uffff\32\u0093"
					+ "\4\uffff\1\u0095\1\uffff\32\u0093",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u00c8\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\u00c9\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u00ca" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\10\57\1\u00cb\21\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\1\43\5\uffff\32\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\2\57\1\u00cc\27\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u00cd\27\57",
			"\1\57\2\uffff\13\57\6\uffff\24\57\1\u00ce\5\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u00cf\16\57",
			"\1\57\2\uffff\13\57\6\uffff\24\57\1\u00d0\5\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\u00d1\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\25\57\1\u00d2\4\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u00d3\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00d4\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00d5\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\22\57\1\u00d6\7\57\4\uffff\1\57"
					+ "\1\uffff\22\57\1\u00d6\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u00d7\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u00d8\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u00d9\10\57",
			"\1\57\2\uffff\13\57\6\uffff\1\u00da\31\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00db\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u00dc\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\25\57" + "\1\u00dd\4\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00de\25\57",
			"\1\57\2\uffff\13\57\6\uffff\25\57\1\u00df\4\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u00e0\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u00e1" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u00e2\24\57",
			"\1\57\2\uffff\13\57\6\uffff\1\u00e3\31\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\2\57\1\u00e4\27\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00e5\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u00e6\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u00e7\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00e8\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u00e9\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u00ea\21\57",
			"\1\57\2\uffff\13\57\6\uffff\17\57\1\u00eb\12\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u00ec\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u00ed\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u00ee\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\u00ef\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u00f0\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u00f1\7\57",
			"\1\57\2\uffff\13\57\6\uffff\22\57\1\u00f2\7\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u00f3\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u00f4\6\57",
			"\1\57\2\uffff\13\57\6\uffff\2\57\1\u00f5\27\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u00f6\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u00f7\6\57",
			"\1\57\2\uffff\13\57\6\uffff\15\57\1\u00f8\14\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\7\57\1\u00f9\22\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\7\57" + "\1\u00fa\22\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u00fb\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\17\57\1\u00fc\12\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\15\57\1\u00fd\14\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u00fe\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\u00ff\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\27\57" + "\1\u0100\2\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\30\57" + "\1\u0101\1\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u0102" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u0103\13\57",
			"\1\57\2\uffff\13\57\6\uffff\2\57\1\u0104\14\57\1\u0105\12\57"
					+ "\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u0106\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u0107\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u0108\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u0109" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\10\57\1\u010a\21\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u010b\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u010c\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u010d\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u010e\7\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u010f\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u0110\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0111\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0112\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u0113\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0114\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0115\14\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\u0116\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u0117\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0118\21\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u0119\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u011a\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\1\u011b\31\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u011c\25\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u011d\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u011e\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\22\57\1\u011f\7\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\22\57\1\u0120\7\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\10\57\1\u0122\21\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0123\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\u0124\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u0125\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\17\57" + "\1\u0126\12\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u0127\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u0128\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u0129\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\30\57" + "\1\u012a\1\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u012b\16\57",
			"\1\57\2\uffff\13\57\6\uffff\3\57\1\u012c\26\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u012d\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u012e\27\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u012f\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0130\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0131\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0132\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0133\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u0134\13\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0135\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u0136\13\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u0137\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u0138\6\57",
			"\1\57\2\uffff\13\57\6\uffff\15\57\1\u0139\14\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\10\57\1\u013a\13\57\1\u013b\5\57"
					+ "\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\17\57\1\u013c\12\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\10\57\1\u013d\13\57\1\u013e\5\57"
					+ "\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"",
			"\1\57\2\uffff\13\57\6\uffff\15\57\1\u013f\14\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\25\57" + "\1\u0140\4\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\u0141\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u0142\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0143\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u0144" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u0145\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0146\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0147\25\57",
			"\1\57\2\uffff\13\57\6\uffff\24\57\1\u0148\5\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\5\57\1\u014a\10\57\1\u0149\13\57"
					+ "\4\uffff\1\57\1\uffff\5\57\1\u014a\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u014b\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\27\57" + "\1\u014c\2\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u014d\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u014e\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u014f\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u0150\6\57",
			"\1\57\2\uffff\13\57\6\uffff\17\57\1\u0151\12\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\5\57\1\u014a\10\57\1\u0152\13\57"
					+ "\4\uffff\1\57\1\uffff\5\57\1\u014a\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0153\14\57",
			"\1\57\2\uffff\13\57\6\uffff\3\57\1\u0154\26\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0155\21\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u0156\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0157\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0158\14\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\u0159\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u015a\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u015b\14\57",
			"\1\57\2\uffff\13\57\6\uffff\6\57\1\u015c\23\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u015d\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u015e\25\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u015f\13\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u0160\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u0161\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\17\57" + "\1\u0162\12\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u0163\27\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0164\14\57",
			"\1\57\2\uffff\13\57\6\uffff\1\u0165\31\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u0166\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\24\57" + "\1\u0167\5\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u0168\13\57\4\uffff\1\57"
					+ "\1\uffff\16\57\1\u0168\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0169\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u016a\6\57",
			"\1\57\2\uffff\13\57\6\uffff\26\57\1\u016b\3\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u016c\6\57",
			"\1\57\2\uffff\13\57\6\uffff\26\57\1\u016d\3\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u016e\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u016f\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u0170" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\25\57" + "\1\u0171\4\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u0172\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0173\21\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u0174\13\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u0175\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0176\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\12\57\1\u0177\6\uffff\32\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u0178\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u0179\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u017a\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u017b\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u017c\25\57",
			"\1\57\2\uffff\12\57\1\u017d\6\uffff\32\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u017e\6\57",
			"\1\57\2\uffff\13\57\6\uffff\13\57\1\u017f\16\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u0180\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u0181\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\25\57" + "\1\u0182\4\57",
			"\1\57\2\uffff\13\57\6\uffff\5\57\1\u0183\24\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0184\21\57",
			"\1\57\2\uffff\13\57\6\uffff\5\57\1\u0185\24\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u0186\21\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\u0187\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u0188\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0189\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u018a\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u018b\13\57",
			"\1\57\2\uffff\13\57\6\uffff\17\57\1\u018c\12\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u018d\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u018e\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u018f\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\30\57" + "\1\u0190\1\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u0191\13\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u0192\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u0193\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u0194\27\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u0195\25\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u0196\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u0197\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u0198\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u0199\6\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u019a\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\12\57\1\u019b\6\uffff\32\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\12\57\1\u019c\6\uffff\32\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u019d\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u019e\14\57",
			"\1\57\2\uffff\13\57\6\uffff\4\57\1\u019f\25\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\21\57" + "\1\u01a0\10\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u01a1\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u01a2\21\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u01a3\13\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u01a4\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u01a5\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u01a6\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u01a7\6\57",
			"\1\57\2\uffff\12\57\1\u01a8\6\uffff\32\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u01a9\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\7\57" + "\1\u01aa\22\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u01ab\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\7\57" + "\1\u01ac\22\57",
			"\1\57\2\uffff\13\57\6\uffff\30\57\1\u01ad\1\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u01ae\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\21\57\1\u01af\10\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\22\57" + "\1\u01b0\7\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u01b1\27\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u01b2\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\30\57" + "\1\u01b3\1\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u01b4\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\u01b5\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\14\57" + "\1\u01b6\15\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u01b7\25\57",
			"\1\57\2\uffff\13\57\6\uffff\23\57\1\u01b8\6\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\4\57" + "\1\u01b9\25\57",
			"\1\57\2\uffff\12\57\1\u01ba\6\uffff\32\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\16\57\1\u01bb\13\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u01bc\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u01bd\27\57",
			"\1\57\2\uffff\13\57\6\uffff\30\57\1\u01be\1\57\4\uffff\1\57" + "\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\2\57" + "\1\u01bf\27\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\5\57" + "\1\u01c0\24\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u01c1\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u01c2\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\23\57" + "\1\u01c3\6\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\1\u01c4" + "\31\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u01c5\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\10\57" + "\1\u01c6\21\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\13\57" + "\1\u01c7\16\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u01c8\13\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\16\57" + "\1\u01c9\13\57",
			"\1\57\2\uffff\12\57\1\u01ca\6\uffff\32\57\4\uffff\1\57\1\uffff" + "\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u01cb\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\15\57" + "\1\u01cc\14\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57",
			"\1\57\2\uffff\13\57\6\uffff\32\57\4\uffff\1\57\1\uffff\32\57" };
	static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
	static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
	static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
	static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
	static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
	static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
	static final short[][] DFA1_transition;
	static {
		int numStates = DFA1_transitionS.length;
		DFA1_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
		}
	}

	class DFA1 extends DFA {
		public DFA1(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 1;
			this.eot = DFA1_eot;
			this.eof = DFA1_eof;
			this.min = DFA1_min;
			this.max = DFA1_max;
			this.accept = DFA1_accept;
			this.special = DFA1_special;
			this.transition = DFA1_transition;
		}

		@Override
		public String getDescription() {
			return "1:1: Tokens : ( DOLLAR | RETURN | SEMICOLON | MOWLLexer. Tokens | OPPLLexer. Tokens );";
		}

		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch (s) {
			case 0:
				int LA1_83 = input.LA(1);
				s = -1;
				if (LA1_83 == '\"') {
					s = 84;
				} else if (LA1_83 >= '\u0000' && LA1_83 <= '!' || LA1_83 >= '#'
						&& LA1_83 <= '\uFFFF') {
					s = 83;
				}
				if (s >= 0) {
					return s;
				}
				break;
			case 1:
				int LA1_143 = input.LA(1);
				s = -1;
				if (LA1_143 == '\"') {
					s = 84;
				} else if (LA1_143 >= '\u0000' && LA1_143 <= '!' || LA1_143 >= '#'
						&& LA1_143 <= '\uFFFF') {
					s = 83;
				}
				if (s >= 0) {
					return s;
				}
				break;
			case 2:
				int LA1_28 = input.LA(1);
				s = -1;
				if (LA1_28 >= '\u0000' && LA1_28 <= '!' || LA1_28 >= '#' && LA1_28 <= '\uFFFF') {
					s = 83;
				} else if (LA1_28 == '\"') {
					s = 84;
				}
				if (s >= 0) {
					return s;
				}
				break;
			}
			NoViableAltException nvae = new NoViableAltException(this.getDescription(), 1, _s,
					input);
			this.error(nvae);
			throw nvae;
		}
	}
}