# org.cosmosgame.sudoku

A sudoku solver done in clojure. The main point is how elegantly clojure lets us work with this kind of problem.
This is the example for my diving into clojure blog: http://www.blogger.com/blogger.g?blogID=4349967400944391189#editor/target=post;postID=1150014284743478152;onPublishedMenu=allposts;onClosedMenu=allposts;postNum=1;src=postname

## Usage

The -main function takes an 81 character string that represents a sudoku board:

(-main "100007090030020008009600500005300900010080002600004000300000010040000007007000300")
[1 6 2 8 5 7 4 9 3 5 3 4 1 2 9 6 7 8 7 8 9 6 4 3 5 2 1 4 7 5 3 1 2 9 8 6 9 1 3 5 8 6 7 4 2 6 2 8 7 9 4 1 3 5 3 5 6 4 7 8 2 1 9 2 4 1 9 3 5 8 6 7 8 9 7 2 6 1 3 5 4]

## License

Copyright © 2012 Odysseus Levy

You are free to use this as you will, I would very much appreciate it if you link to my blog if you do use this.
