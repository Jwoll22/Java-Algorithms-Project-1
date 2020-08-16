import java.io.*;
import java.util.*;

public class Crossword
{
    public Letter[][] board;
    public Word[][] colStr;
    public Word[][] rowStr;
    public DictInterface dict;
    public int size;
    public int level;

    private PauseClass pauser;
    private boolean paused;
    private int sols;

    public Crossword(int size, StringBuilder inp, String dictType) throws IOException
    {
        this.size = size;
        level = 0;
        board = new Letter[size][size];
        colStr = new Word[size][(size+1)/2];
        rowStr = new Word[size][(size+1)/2];

        if (dictType.equals("DLB"))
            dict = new DLB();
        else
            dict = new MyDictionary();

        int rowCount, colCount, rowwordLength, colwordLength;

        Scanner fileScan = new Scanner(new FileInputStream("dict8.txt"));
        String st = "";
        while (fileScan.hasNext())
		{
			st = fileScan.nextLine();
			dict.add(st);
		}


        //  Block to initialize board into open blocks, closed blocks, and already filled out spaces
        for (int i = 0; i < size; i++)
        {
            rowCount=0; colCount=0; rowwordLength=0; colwordLength=0;
            for (int j = 0; j < size; j++)
            {
                //  Start up rowStr
                if (rowwordLength>0 && (inp.charAt(j+(size)*i) == '-' || j == size-1))
                {
                    rowStr[i][rowCount] = new Word(rowwordLength, j-rowwordLength);
                    rowCount++;
                    if (rowCount < (size+1)/2)    rowStr[i][rowCount] = new Word();
                    rowwordLength = 0;
                }
                else    rowwordLength++;
                 //  **Don't know how to treat preinserted characters, so tryInsert will have to reach them before safe() can react**

                //  Start up colStr
                if (colwordLength>0 && (inp.charAt(i+(size)*j) == '-' || j == size-1))
                {
                    colStr[i][colCount] = new Word(colwordLength, j-colwordLength);
                    colCount++;
                    if (colCount < (size+1)/2)    colStr[i][colCount] = new Word();
                    colwordLength = 0;
                }
                else    colwordLength++;

                //  Start up board
                if (inp.charAt(j+(size)*i) == '-')  board[i][j] = new Letter(false, (char)45);
                else if (inp.charAt(j+(size)*i) == '+')
                    board[i][j] = new Letter(true);
                else    //  Insert starting letter
                    board[i][j] = new Letter(false, inp.charAt(j+(size)*i));

            }
        }

        //  Block to solve the crossword
        paused = false;
        pauser = new PauseClass();
        tryInsert(0,0,dictType);
    }



    public void print(int startRow, int startCol)
    {
        for (int i = startRow; i < size; i++)
        {
            for (int j = startCol; j < size; j++)
            {
                System.out.print(board[i][j].letter + " ");
            }
            System.out.println();
        }
    }


    public boolean safe(int row, int col, char letter)
    {
        int safeCol = 0;
        int safeRow = 0;
        int indexCol = 0;
        int indexRow = 0;

        //  Find the right word
        for (int j = 0; j < (size+1)/2; j++)
        {
            if (colStr[col][j].start <= row && row < (colStr[col][j].start + colStr[col][j].length))
            {    indexCol = j;  break;  }
        }


        for (int j = 0; j < (size+1)/2; j++)
        {
            //System.out.println("j: " + j);
            //System.out.println(rowStr[row][j]);
            if (rowStr[row][j].start <= col && col < (rowStr[row][j].start + rowStr[row][j].length))
            {    indexRow = j;  break;  }
        }

        //  For permanent letters
        if (!board[row][col].plusminus && board[row][col].letter != (char)45)
        {
             colStr[col][indexCol].add(board[row][col].letter);
             rowStr[row][indexRow].add(board[row][col].letter);
             safeCol = dict.searchPrefix(colStr[col][indexCol].str, colStr[col][indexCol].length);
             safeRow = dict.searchPrefix(rowStr[row][indexRow].str, rowStr[row][indexRow].length);
             colStr[col][indexCol].remove();
             rowStr[row][indexRow].remove();
        }
        //  For open slots
        else if (board[row][col].plusminus)
        {
            colStr[col][indexCol].add(letter);
            rowStr[row][indexRow].add(letter);
            safeCol = dict.searchPrefix(colStr[col][indexCol].str, colStr[col][indexCol].length);
            safeRow = dict.searchPrefix(rowStr[row][indexRow].str, rowStr[row][indexRow].length);
            colStr[col][indexCol].remove();
            rowStr[row][indexRow].remove();
        }
        else if (!board[row][col].plusminus)
            return true;

        //System.out.println("safeCol: " + safeCol);
        //System.out.println("safeRow: " + safeRow);
        if (row == size-1 && col == size-1)    return safeCol>0 && safeCol <= 2 && safeRow>0 && safeRow <= 2;
        else if (row == size-1) return safeCol>0 && safeCol <= 2 && safeRow >= 2;
        else if (col == size-1) return safeRow>0 && safeRow <= 2 && safeCol >= 2;
        else    return safeCol >= 2 && safeRow >= 2;
    }


    public void tryInsert(int row, int col, String dictType)
    {
        //System.out.println(row + ", " + col);
        Character letter = (char)0;
        int doneBacktracking = 0;
        //boolean failed = false;   //  Tracks if an index has tried all letters and failed
        //int  goBack = 0;    //  Determines if success backtracking is necessary based on 'failed' and the column we're in
        //System.out.println("level: " + level);
        level++;

        for (int i = 0; i < 26; i++)
        {
            letter = (char)(i + 97);
            //System.out.println("letter: " + letter);
            if (safe(row, col, letter))   //  If there is a closed block or it started with a letter, enter the loop immdiately
            {
                //System.out.println("Letter: " + letter);
                // Block to insert a new safe character
                // and update arrays
                //failed = false;
                if (board[row][col].plusminus || board[row][col].letter == (char)0)  //  If there is a closed block, don't add anything; UPDATE IF THIS BLOCK STARTED WITH A LETTER
                {
                    board[row][col].update(letter, level);
                    //print(0,0);
                    for (int j = 0; j < (size+1)/2; j++)
                    {
                            if (colStr[col][j].start <= row && row < colStr[col][j].start + colStr[col][j].length)
                            {    colStr[col][j].add(letter);  break;  }
                    }

                    for (int j = 0; j < (size+1)/2; j++)
                    {
                        if (rowStr[row][j].start <= col && col < rowStr[row][j].start + rowStr[row][j].length)
                        {    rowStr[row][j].add(letter);  /*System.out.println("rowStr[ " + row + "]: "+ rowStr[row][j].str);*/ break;  }
                    }
                }

                if (row == size-1 && col == size-1)
                {
                    if (dictType.equals("DLB"))
                    {
                        if (sols == 0 % 10000)
                            print(0,0);
                    }
                    else
                    {
                        print(0,0);
                        sols++;
                        System.out.println("Solutions found:" + sols);
                        System.exit(1);
                    }
                    sols++;
                    System.out.println("Solutions found:" + sols);
                }
                else if (doneBacktracking == 0)
                {
                    try { Thread.sleep(100); }
                    catch (InterruptedException e)  { System.out.println("Thread error B"); }


                    if (col == size-1)  tryInsert(row+1, 0, dictType);
                    else tryInsert(row, col+1, dictType);
                }

                level--;
                //if (doneBacktracking > 0) doneBacktracking--;

                // Block to remove character due to error
                // and update arrays
                if (board[row][col].plusminus)  //  If there is a closed block or it started with a letter, don't remove anything
                {
                    board[row][col].update((char)0, level);
                    for (int j = 0; j < size/2; j++)
                    {
                            if (colStr[col][j].start <= row && row < colStr[col][j].start + colStr[col][j].length)   colStr[col][j].remove();
                            if (rowStr[row][j].start <= col && col < rowStr[row][j].start + rowStr[row][j].length)   rowStr[row][j].remove();
                    }
                }

            //  Determine if successive bounds are necessary
            /*failed = i == 25 && !safe(row, col, letter);
            if (failed && col >= 0)    goBack = col;
            if (failed && col == 0 && row > 0) doneBacktracking = level - board[row-1][goBack].level;
            */
            }
        }
    }


    public static void main(String[] args) throws IOException
    {
        Scanner fileScan = new Scanner(new FileInputStream("test3a.txt"));
        String dictType = "DLB";
        int size = Integer.parseInt(fileScan.nextLine());
        System.out.println(size);
        StringBuilder st = new StringBuilder();

        while (fileScan.hasNext())
		{
			st.append(fileScan.nextLine());
        }

        Crossword c = new Crossword(size, st, dictType);
    }

    private class PauseClass
	{
		public synchronized void pause()
		{
			paused = true;
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				System.out.println("Pause Problem");
			}
		}

		public synchronized void unpause()
		{
			paused = false;
			notify();
		}
	}
}
