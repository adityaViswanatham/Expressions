// Author: Aditya Viswanatham.

package arv160730;

import java.util.*;
import java.io.*;

/** Class to store a nodes of expression tree.
    For each internal node, element contains a binary operator.
    List of operators: +|*|-|/|%|^.
    Other tokens: (|).
    Each leaf node contains an operand (long integer).
*/

// Expression Class to hold the nodes(tokens) for the expression tree.
public class Expression {
	
    // Enumeration for all the known constants(operands/operators).
    public enum TokenType {  
    	// NIL is a special token that can be used to mark bottom of stack
    	PLUS, TIMES, MINUS, DIV, MOD, POWER, OPEN, CLOSE, NIL, NUMBER
    }
    
    /** Class to store the information inside the nodes required for the expression tree.
     	Attributes include a token which is used to identify which element it is.
     	The priority attribute which stores the priority associated with each operator.
     	The number attribute which contains a long value of an operand.
     	The string attribute which stores the character or a list of characters to be used 
     	inside the loops of functions.
     */
    
    // Token Class to form the nodes of the expression tree.
    public static class Token {
    	TokenType token;
    	int priority; // for precedence of operator
    	Long number;  // used to store number of token = NUMBER
    	String string;
    	
    	// Constructor for operators. Not for numbers.
    	Token(TokenType op, int pri, String tok) {
    		token = op;
    		priority = pri;
    		number = null;
    		string = tok;
    	}

    	// Constructor for numbers. To be called when other options have been exhausted.
    	Token(String tok) {
    		token = TokenType.NUMBER;
    		number = Long.parseLong(tok);
    		string = tok;
    	}
    	
    	// Returns a boolean value when the token is a number.
    	boolean isOperand() { 
    		return token == TokenType.NUMBER; 
    	}
    	
    	// Returns the number based on the isOperand() function.
    	public long getValue() {
    		return isOperand() ? number : 0;
    	}
    	
    	// Returns the string.
    	public String toString() { 
    		return string; 
    	}
    }
    
    // Attributes of the Expression Class.
    Token element;
    Expression left, right;
    
    // Function to covert a string into a list of tokens.
    static Token getToken(String tok) {  
    	// Check for priority.
    	Token result;
    	switch(tok) {
    	case "+":
    		result = new Token(TokenType.PLUS, 2, tok);  
    		break;
    	case "-":
    		result = new Token(TokenType.MINUS, 2, tok);  
    		break;
    	case "*":
    		result = new Token(TokenType.TIMES, 3, tok);  
    		break;
    	case "/":
    		result = new Token(TokenType.DIV, 3, tok); 
    		break;
    	case "%":
    		result = new Token(TokenType.MOD, 3, tok);  
    		break;
    	case "^":
    		result = new Token(TokenType.POWER, 4, tok);  
    		break;
    	case "(":
    		result = new Token(TokenType.OPEN, 1, tok);  
    		break;
    	case ")":
    		result = new Token(TokenType.CLOSE, 1, tok);  
    		break;
    	// Default case for numbers (operands).
    	default:
    		result = new Token(tok);
    		break;
    	}
    	return result;
    }
    
    // Default Constructor.
    private Expression() {
    	element = null;
    }
    
    // Constructor for all constants other than numbers.
    private Expression(Token oper, Expression left, Expression right) {
    	this.element = oper;
    	this.left = left;
    	this.right = right;
    }

    // Constructor for numbers.
    private Expression(Token num) {
    	this.element = num;
    	this.left = null;
    	this.right = null;
    }

    // Infix to Expression Tree. Takes a list of tokens in infix and builds an expression tree.
    public static Expression infixToExpression(List<Token> exp) {
    	Expression root = null;
    	// Calling the infix to post-fix function to facilitate tree building.
    	List<Token> p_list = infixToPostfix(exp);
    	Stack<Expression> the_stack = new Stack<>();
    	ListIterator<Token> l_iter = p_list.listIterator();
    	// Loop to iterate through the list.
    	while(l_iter.hasNext()) {
    		Token element = l_iter.next();
    		// Check for an operand.
    		if(element.isOperand()) {
    			// Creating a new Expression object and adding to the stack.
    			the_stack.add(new Expression(element));
    		}
    		// For an operator.
    		else {
    			Expression right = the_stack.pop();
    			Expression left = the_stack.pop();
    			// Creating a new Expression object and adding to the stack.
    			the_stack.add(new Expression(element, left, right));
    		}
    	}
    	// Getting the root of the tree.
    	root = the_stack.peek();
    	return root;
    	
    	// Another way to construct the expression tree without calling the infix to post-fix function.
    	/*
    	Stack<Token> opStack = new Stack<>();
        Stack<Expression> thestack = new Stack<>();
        Iterator<Token> l_iter = exp.iterator();
        while(l_iter.hasNext()) {
            Token element = l_iter.next();
            if(element.isOperand()) {
                thestack.add(new Expression(element));
            }
            else {
                if(!opStack.isEmpty()) {
                    if(element.priority <= opStack.peek().priority) {
                        Token el = opStack.pop();
                        Expression right = thestack.pop();
                        Expression left = thestack.pop();
                        thestack.add(new Expression(el, left, right));
                        opStack.push(element);
                    }
                    else {
                        opStack.push(element);
                    }
                }
                else {
                    opStack.push(element);
                }
            }
        }
        while(!opStack.isEmpty()) {
            Token pop_tok = opStack.pop();
            Expression right = thestack.pop();
            Expression left = thestack.pop();
            thestack.push(new Expression(pop_tok, left, right));
        }
        return thestack.pop();
        */
    }

    // Infix to post-fix expressions. Takes a list of tokens and converts into post-fix.
    public static List<Token> infixToPostfix(List<Token> exp) { 
    	List<Token> out_list = new LinkedList<>();
    	Stack<Token> the_stack = new Stack<>();
    	ListIterator<Token> l_iter = exp.listIterator();
    	// Loop to iterate through the list.
    	while(l_iter.hasNext()) {
    		Token element = l_iter.next();
    		// If the element is an operand add to output list.
    		if(element.isOperand()) {
    			out_list.add(element);
    		}
    		// If element is an Open Bracket push into the stack.
    		else if(element.token.compareTo(TokenType.OPEN) == 0) {
    			the_stack.push(element);
    		}
    		// If the element is a Closing Bracket pop operators until Open Bracket from stack.
    		else if(element.token.compareTo(TokenType.CLOSE) == 0) {
    			// Checking to see if the pointer hits an opening parenthesis.
    			while(!the_stack.isEmpty() && the_stack.peek().token.compareTo(TokenType.OPEN)!=0) {
    				out_list.add(the_stack.pop());
    			}
    			// For the opening parenthesis.
    			the_stack.pop();
    		}
    		// If the element is an operator.
    		else {
    			// Checking for priority to see if an operator needs to be popped.
    			while(!the_stack.isEmpty() && element.priority <= the_stack.peek().priority) {
    				out_list.add(the_stack.pop());
    			}
    			the_stack.push(element);
    		}
    	}
    	// Popping out remaining operators from the stack and adding them to the list.
    	while(!the_stack.isEmpty()) {
    		// Adding remaining operators to the list.
    		out_list.add(the_stack.pop());
    	}
    	// Returning the list. 
    	return out_list;
    }

    // Function to evaluate a post-fix expression. It takes a List and returns a long after evaluating the post-fix list.
    public static long evaluatePostfix(List<Token> exp) { 
    	Stack<Long> op_stack = new Stack<>(); 
    	ListIterator<Token> l_iter = exp.listIterator();
    	// Loop to iterate through the list.
    	while(l_iter.hasNext()) {
    		Token element = l_iter.next();
    		// Checking to see if the token element is an operand.
    		if(element.number != null) {
    			op_stack.push(element.number);
    		}
    		// When the pointer hits an operator.
    		else {
    			// Popping two operands to perform operations based on the operator.
    			long op1 = op_stack.pop();
    			long op2 = op_stack.pop();
    			if(element.token.compareTo(TokenType.PLUS) == 0) {
    				op_stack.push(op1 + op2);
    			}
    			else if(element.token.compareTo(TokenType.MINUS) == 0) {
    				op_stack.push(op2 - op1);
    			}
    			else if(element.token.compareTo(TokenType.TIMES) == 0) {
    				op_stack.push(op1 * op2);
    			}
    			else if(element.token.compareTo(TokenType.DIV) == 0) {
    				op_stack.push(op2 / op1);
    			}
    			else if(element.token.compareTo(TokenType.MOD) == 0) {
    				op_stack.push(op2 % op1);
    			}
    			else {
    				// Casting a double returned by the Math.pow function to a long.
    				long pow_result = (long)(Math.pow(op2, op1));
    				op_stack.push(pow_result);
    			}
    		}
    	}
    	// Popping and returning the last element in the stack which is the end result.
    	return op_stack.pop();
    }

    // Function to evaluate expression trees. It takes a tree and returns a long value after evaluation.
    public static long evaluateExpression(Expression tree) { 
    	// Checking to see if the given node is a leaf node.
    	if(tree.left == null && tree.right == null) {
    		return tree.element.number;
    	}
    	// If the node has children.
    	else {
    		Long var1 = evaluateExpression(tree.left);
    		Long var2 = evaluateExpression(tree.right);
    		// Performing operations based on the operator in the root of the subtree.
    		if(tree.element.toString().equals("+")) {
    			return var1 + var2;
    		}
    		else if(tree.element.toString().equals("-")) {
    			return var1 - var2;
    		}
    		else if(tree.element.toString().equals("*")) {
    			return var1 * var2;
    		}
    		else if(tree.element.toString().equals("/")) {
    			return var1 / var2;
    		}
    		else if(tree.element.toString().equals("%")) {
    			return var1 % var2;
    		}
    		else {
    			return (long)Math.pow(var1, var2);
    		}
    	}
    }

    // Main file to check the functionality of the program.
    public static void main(String[] args) throws FileNotFoundException {
    	Scanner in;
    	if (args.length > 0) {
    	    File inputFile = new File(args[0]);
    	    in = new Scanner(inputFile);
    	} 
    	else {
    	    in = new Scanner(System.in);
    	}
    	int count = 0;
    	while(in.hasNext()) {
    	    String s = in.nextLine();
    	    List<Token> infix = new LinkedList<>();
    	    Scanner sscan = new Scanner(s);
    	    int len = 0;
    	    while(sscan.hasNext()) {
    		infix.add(getToken(sscan.next()));
    		len++;
    	    }
    	    if(len > 0) {
    		count++;
    		System.out.println("Expression number: " + count);
    		System.out.println("Infix expression: " + infix);
    		// Calling the above defined functions.
    		Expression exp = infixToExpression(infix);
    		List<Token> post = infixToPostfix(infix);
    		// Printing out the post-fix expression.
    		System.out.println("Postfix expression: " + post);
    		// Calling the above defined functions.
    		long pval = evaluatePostfix(post);
    		long eval = evaluateExpression(exp);
    		// Printing out the post-fix evaluation and the expression tree evaluation.
    		System.out.println("Postfix eval: " + pval + " Exp eval: " + eval + "\n");
    		
    	    }
    	}
    	in.close();
    }
}
