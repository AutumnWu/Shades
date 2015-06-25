package eclipsePackage;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.Random;

public class Shades extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		final JFrame frame = new JFrame("Shades");  //Construct a new JFrame
		final Board a = new Board();				//Construct a Board called a
		frame.addKeyListener(a);					//Add a keyListener to JFrame
		frame.add(a);			//Add Board a to frame	
		String[] objects = {"Easy","Medium","Hard"};
		int t = 60;
		int  choice = JOptionPane.showOptionDialog(frame,"Easy, Medium or Hard?","Choose a level",JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,objects,"Easy");
		if(choice == 0) t = 60;
		if(choice == 1) t = 30;
		if(choice == 2) t = 10;
		final Timer timer = new Timer(t, a.new TimerListener());  	//Construct a timer.
		timer.start();												//start time
		JMenuBar menu = new JMenuBar();    							//Construct a MenuBar
		frame.setJMenuBar(menu);
		JMenu gameMenu = new JMenu("Game");						
		JMenuItem newitem = new JMenuItem("New Game");
		gameMenu.add(newitem);
		final JMenuItem pauseitem = new JMenuItem("Pause");
		gameMenu.add(pauseitem);
		JMenuItem contitem = new JMenuItem("Continue");
		gameMenu.add(contitem);
		JMenuItem exititem = new JMenuItem("Exit");
		gameMenu.add(exititem);
		JMenuItem highscores = new JMenuItem("High Scores");
		gameMenu.add(highscores);	
		
		
		//implement New Game method
		newitem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				a.newmap();
				a.drawwall();
				a.score = 0;
				a.x = a.random.nextInt(3)*50+50;
				a.y = -30;
				a.Vely = 2;
				a.y = a.y + a.Vely;
				a.color = a.random.nextInt(5);
				a.nextC = a.random.nextInt(5);
			}
		});
		
		//implement Pause method
		pauseitem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				timer.stop();
				pauseitem.setEnabled(false);
			}
			});
		
		//implement Continue method
		contitem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				timer.start();
				pauseitem.setEnabled(true);
			}
			});
		
		//implement Exit method
		exititem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				timer.stop();
				System.exit(0);
			}
			});
		
		
		
		menu.add(gameMenu);						//add gameMwnu to menu
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //Enable user to exit
		frame.setBounds(300, 120, 200, 345);			
		frame.setVisible(true);
		frame.setResizable(false);	
		}
		
	}


	//Construct board class
	class Board extends JPanel implements KeyListener{
		private static final long serialVersionUID = 1L;
		Random random = new Random();		//Random method
		public int color;					//color of the current rectangle.
		public int score = 0;	//game score
		public int hscore = 0;
		public int x;						
		public int y;
		public int Vely;					//Velocity of y.
		public boolean ismerge;				//boolean variable indicating whether merge happened or not
		public boolean isdown;				//boolean variable indicating whether the user pressed key_down or not.
		public int nextC = random.nextInt(5);	//color of the next rectangle
		private int i=0;
		private int j=0;
		private boolean flag = false;
		int[][] map = new int[11][6];		//Record current situation of the whole board.
		int[][] Reccolor = new int[11][6];	//Record the colors of existing rectangles
		
		
		Board(){
			newblock();
			newmap();
			drawwall();
		}
		
		//the color array
		private final Color[] Blockcolors = new Color[]{
				Color.decode("#CCE5FF"), 
				Color.decode("#66B2FF"),
				Color.decode("#0080FF"),
				Color.decode("#004C99"),
				Color.decode("#003366"),
		};
		
		//method of generating a new block
		public void newblock (){
			color = random.nextInt(5);   //Randomly assign a color to it
			//nextC = random.nextInt(5);
			x = random.nextInt(4)*50+50; //Randomly decide the start x position of it
			y = 0;
			y += Vely;					//make it move;
			isdown = false;				//set isdown to false
			repaint();					//repaint
			if (gameover(x,y)){ 		//if gameover then start a new game.
				if(score >= hscore) hscore = score;
				JOptionPane.showMessageDialog(null, "Game Over! \nHighest score:"+ hscore);
				newmap();
				drawwall();
				score = 0;	
			}
		}
		
		//Method of deciding whether the game is over or not
		public boolean gameover (int x, int y){
			if(blow(x,y)){  //if the newly constructed block already has a block below it, which means it cannot go down anymore.
				add(x,y,color);
				if(Reccolor[y/30][x/50]!=Reccolor[y/30+1][x/50]){//and they can not merge
					return true;   //then game is over.
				}else return false; //if they can merge, then game continues.
			}else return false;   
		}
		
		//To see whether the given block has a block below or it reaches the bottom of the board or it went out of the board
		public boolean blow(int x, int y){
			int a = x/50;
			int b = y/30;
			if(map[b+1][a] == 1||map[b+1][a] == 2||map[b][a] == 2){
				return true;
			}else return false;
		}

		//initialize our map and reccolor
		public void newmap(){
			for(int i=0; i<11;i++){
				for(int j=0;j<6;j++){
					map[i][j] = 0;
					Reccolor[i][j] = -1;
				}
			}
		}

		//draw a wall around the play area for later check 
		public void drawwall(){
			for(int i =0; i<11;i++){
				map[i][5] = 2;
				map[i][0] = 2;
			}
			for(int j=0; j<6;j++){
				map[10][j] = 2;
			}
		}

		//go left
		public void left(){
			if(!blow(x-50,y)&&!blow(x,y)){ //if the block can go left and doesn't reach a another block or the bottom
				x = x-50;	//then it goes left
			}
			repaint();		//repaint it
		}

		//go right (similar to left)
		public void right(){
			if(!blow(x+50,y)&&!blow(x,y)){
				x = x+50;
			}
			repaint();
		}
		
		//go down
		public void down(){
			isdown = true;  //isdown turns to right
			
			//This part seems to have no effects on my game, so I turned them into comments
            
///			if(!blow(x,y)){  //if it can still goes down
	//			y = y+1;    //goes down
		//	}
		//	if(blow(x,y)){		//if it reaches its bottom
			//	int row = y/30;
			//	int col = x/50;
			//	int r;
				
			//	add(x,y,color); //add this block
			//	for(r=row;r<9;r++){ //loop through and then merge if they satisfy the rule.
			//		if(Reccolor[r][col]==Reccolor[r+1][col]&&Reccolor[r][col]!=4){  
			//			merge(x,r*30); 	
						
			//		}else break; //if can not merge, break the loop
			//	}
				
			//	newblock();   //construct a new block
			//}
			//delline();     //see if we can delete a line.
			
			
			repaint();	   //repaint
		}
		
		//merge
		public void merge(int x,int y){
			map[y/30][x/50] = 0;		//the upper block disappears from the map
			Reccolor[y/30][x/50] = -1;	//set its color back to -1;
			Reccolor[y/30+1][x/50] ++;	//increase the color of lower block by one
			score += 20;				//increase score by 20
			//repaint();
		}
		
		//delete a line
		public void delline(){
			int c = 0;   
			int c1= 0;
			int b;
			for(int a=0; a<10; a++){ //for each row
				c = 0;
				c1 =0;
				for(b=1;b<5;b++){ 	 //loop through each column
					c = c + Reccolor[a][b];			//add their colors
					c1 = c1 + map[a][b];			//add their map variable
				}	
				//if they all have the same color and every column has a block
				if(c==4*Reccolor[a][4]&&c==4*Reccolor[a][3]&&c==4*Reccolor[a][2]&&c==4*Reccolor[a][1]&&c1==4){
					score = score + 50;  //increase score by 50
					for(int d=a; d>0;d--){   //for rows above the the satisfying row
						for(int e=1;e<5;e++){	//for each column							
							map[d][e] = map[d-1][e];  //change the status of the line to the line above it
							Reccolor[d][e] = Reccolor[d-1][e];
						}
					}
				}
			}
			repaint();  //repaint it 
		}
		 
		//add the block to map and reccolor
		public void add(int x,int y, int color){
			map[y/30][x/50] = 1;
			Reccolor[y/30][x/50] = color;
		}
		
		//how to paint
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			//paint those fixed blocks
			for(j=0;j<11;j++){
				for(i=0;i<6;i++){
					if(map[j][i] == 1){
						g.setColor(Blockcolors[Reccolor[j][i]]);
						g.fillRect(i*50-50, j*30, 50, 30);
						g.drawRect(i*50-50, j*30, 50, 30);
					}
				}
			}
			//paint the current block
			g.setColor(Blockcolors[color]);
			g.fillRect(x-50,y,50,30);
			g.drawRect(x-50,y,50,30);
			
			//paint score
			g.setColor(Color.black);
			g.drawString("Score:"+score,100,10);
			//super.paintComponent(g);
			//timer.start();
		}
		
		//Listen to the keyboard
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_DOWN){
				down();
			}
			if(e.getKeyCode() == KeyEvent.VK_LEFT){
				left();
			}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				right();
			}
		}
		
		//reserve for future use
		public void keyReleased(KeyEvent e){
		}
		//reserve for future use
		public void keyTyped(KeyEvent e){
		}
		
		//Listen to timer
		class TimerListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				repaint();
				Vely = 1;
				if(isdown){   //if user pressed down, the velocity becoming 8
					Vely = 8;
				}
				if(!blow(x,y)){ //if the new block is still going down see if we can delete a existing line.
					delline();
				}
				if(y>270) y = 270;  //if the block goes beyond the bottom, set it back.
				if(blow(x,y)){		//if the block reaches its own bottom
					Vely = 0;		//stop moving 
					if(flag == true){  
						delline(); //delete a line if possible
						int row = y/30;
						int col = x/50;
						int r;
						add(x,y,color);  //add the block
						for(r=row;r<9;r++){   //merge all satisfying blocks
							if(Reccolor[r][col]==Reccolor[r+1][col]&&Reccolor[r][col]!=4){
								merge(x,r*30);
							}else break;
						}     
						//delline();//delete a line again if possible
						newblock();	   //construct a new block
						flag = false;
					}
				}
				flag = true;
				y = y+Vely;				//block keeps going down at speed Vely.
			}
		}
}
	


