public class Word
{
    public StringBuilder str;
    public int length;  // Potential length of the word
    public int start;   //  Starting index

    public Word()
    {
        this.str = new StringBuilder();
        this.length = 0;
        this.start = 0;
    }

    public Word(int length, int start)
    {
        this.str = new StringBuilder();
        this.length = length;
        this.start = start;
    }

    public Word(StringBuilder s, int length, int start)
    {
        this.str = s;
        this.length = length;
        this.start = start;
    }

    public void add(char s)
    {
        this.str.append(s);
    }

    public void remove()
    {
        this.str.deleteCharAt(this.str.length()-1);
    }
}
