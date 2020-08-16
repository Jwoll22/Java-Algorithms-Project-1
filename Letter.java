public class Letter
{
    public boolean plusminus;
    public char letter;
    public int level;

    public Letter(boolean plusminus)
    {
        this.plusminus = plusminus;
        this.letter = (char)0;
        this.level = 0;
    }

    public Letter(char letter)
    {
        this.letter = letter;
        this.plusminus = false;
        this.level = 0;
    }

    public Letter(boolean plusminus, char letter)
    {
        this.plusminus = plusminus;
        this.letter = letter;
        this.level = 0;
    }

    public void update(char l, int lvl)
    {
        this.letter = l;
        this.level = lvl;
    }
}
