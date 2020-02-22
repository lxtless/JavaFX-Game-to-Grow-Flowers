package sample;

/*
    Author: lxtless
    Start: 2019.6.25
	End: 2019.7.6
 */

import javafx.animation.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import static java.lang.Math.random;

class MyPoint {
    public double x,y;
    public MyPoint() {
        x = 0;
        y = 0;
    }
    public MyPoint(double xx,double yy) {
        x = xx;
        y = yy;
    }

    public void setXandY(MyPoint a) {
        x = a.x;
        y = a.y;
    }
    public void setX(double xx) {
        x = xx;
    }
    public void setY(double yy) {
        y = yy;
    }
}

/*
    花瓣类，一个实例表示一圈花瓣
 */
class Petal {
    private Color pcolor,scolor;    //花瓣颜色，轮廓颜色
    private double pchange,schange; //一圈花瓣内花瓣颜色偏移量，轮廓颜色偏移量
    private int num;                //一圈花瓣个数
    private double length;          //花瓣径向长度
    private double width;           //花瓣横向宽度
    private double place;           //花瓣横向最大宽度在花瓣上的位置,0<place可以大于length
    private double strokewidth;     //轮廓宽度

    public Petal() {//默认黑色花
        pcolor = Color.BLACK;
        scolor = Color.BLACK;
        pchange = 0;
        schange = 0;
        num = 4;
        length = 20;
        width = 20;
        place = 20;
        strokewidth = 1;
    }

    /*
        创建不同特定品种的花瓣
     */
    public Petal(int breedNumber) {
        switch(breedNumber) {
            case 1:     //向日葵
                pcolor = Color.YELLOW;
                scolor = Color.GRAY;
                pchange = 0;
                schange = 0;
                num = 15;
                length = 30;
                width = 6;
                place = 25;
                strokewidth = 1;
                break;
            case 2:     //随机彩虹花
                pcolor = Color.color(random(),random(),random());
                scolor = Color.color(random(),random(),random());
                pchange = 0.2;
                schange = 0.2;
                num = 6;
                length = 20;
                width = 6;
                place = 10;
                strokewidth = 1;
                break;
            case 3: //三瓣花
                pcolor = Color.RED;
                scolor = Color.PINK;
                pchange = 0.5;
                schange = 0.1;
                num = 3;
                length = 16;
                width = 16;
                place = 8;
                strokewidth = 1;
                break;
            case 4: //多层花
                pcolor = Color.SNOW;
                scolor = Color.BLUEVIOLET;
                pchange = 0;
                schange = 0;
                num = 6;
                length = 14;
                width = 10;
                place = 16;
                strokewidth = 1;
                break;
            default:    //默认黑色花
                pcolor = Color.BLACK;
                scolor = Color.BLACK;
                pchange = 0;
                schange = 0;
                num = 1;
                length = 20;
                width = 20;
                place = 20;
                strokewidth = 1;
        }
    }

    /*
        通过一层花瓣构造下一层花瓣，颜色改变量
     */
    public Petal(Petal p,Color cchange) {
        pcolor = Color.color((p.pcolor.getRed()+cchange.getRed())%1,(p.pcolor.getGreen()+cchange.getGreen())%1,(p.pcolor.getBlue()+cchange.getBlue())%1);
        scolor = p.scolor;
        pchange = p.pchange;
        schange = p.schange;
        length = p.length+10;
        place = p.place/p.length*length;
        double a = 3.1416/Math.atan(p.width/place);
        num = (int)a;
        double angle = 3.14159/num;
        width = place*Math.tan(angle)+1;
        strokewidth = p.strokewidth;
    }
    /*
        通过父本和母本构造花瓣
     */
    public Petal(Petal fa,Petal mo) {
        double r,g,b;
        r = (2*fa.pcolor.getRed()+mo.pcolor.getRed())/3;
        g = (2*fa.pcolor.getGreen()+mo.pcolor.getGreen())/3;
        b = (2*fa.pcolor.getBlue()+mo.pcolor.getBlue())/3;
        pcolor = Color.color(r,g,b);
        r = (2*fa.scolor.getRed()+mo.scolor.getRed())/3;
        g = (2*fa.scolor.getGreen()+mo.scolor.getGreen())/3;
        b = (2*fa.scolor.getBlue()+mo.scolor.getBlue())/3;
        scolor = Color.color(r,g,b);
        pchange = (fa.pchange+mo.pchange)/2;
        schange = (fa.schange+mo.schange)/2;
        num = (fa.num+mo.num)/2;
        length = (fa.length+mo.length)/2;
        width = (fa.width+mo.width)/2;
        place = (fa.place+mo.place)/2;
        strokewidth = (fa.strokewidth+mo.strokewidth)/2;
        Random rand = new Random();
        if((fa.equals(mo) && (random()>0.5)) || random()>0.8) {//产生变异，自交概率更大
            int t = rand.nextInt(6);
            if(t < 1) {
                pcolor = Color.color(random(),random(),random());
            }
            else if(t < 2) {
                scolor = Color.color(random(),random(),random());
            }
            else if(t < 3) {
                pchange += rand.nextInt(2)*0.2 - 0.1;
                if(pchange <= 0)    pchange = 0.1;
            }
            else if(t < 4) {
                schange += rand.nextInt(2)*0.2 - 0.1;
                if(schange <= 0) schange = 0.1;
            }
            else if(t < 5) {
                num += rand.nextInt(2)*2 - 1;
                if(num < 2)  num = 2;
            }
            else if(t < 6) {
                length += rand.nextInt(2)*4 - 2;
                if(length < 5)  length = 5;
            }
            else if(t < 7) {
                width += rand.nextInt(2)*4 - 2;
                if(width < 2)   width = 2;
            }
            else if(t < 8) {
                place += rand.nextInt(2)*6 - 3;
                if(place < 2)   place = 2;
            }
            else if(t < 9) {
                strokewidth += rand.nextInt(2)*2 - 1;
                if(strokewidth < 1) strokewidth = 1;
            }

        }
    }

    /*
        绘制花瓣
        传入参数：花瓣中心点位置，花瓣起始角度，画布实例
     */
    public void paint(MyPoint o,double startd,GraphicsContext gc) {
        String SVG;//花瓣外形的SVG路径
        double angle = 3.14159/num;//每个花瓣张角的1/2
        //根据花瓣宽度实际情况确定实际宽度w，避免花瓣互相重合
        double w = (width/place > Math.tan(angle))?place*Math.tan(angle)*1.25:width;
        //double w = width;

        for(int i=0;i<num;i++){
            double a = startd+angle*2*i;//花瓣轴向角度
            MyPoint endp = new MyPoint(o.x+length*Math.cos(a),o.y+length*Math.sin(a));
            MyPoint midp = new MyPoint(o.x+place*Math.cos(a),o.y+place*Math.sin(a));
            MyPoint one = new MyPoint(midp.x+w*Math.sin(a),midp.y-w*Math.cos(a));
            MyPoint two = new MyPoint(midp.x-w*Math.sin(a),midp.y+w*Math.cos(a));
            SVG = new String("Q "+one.x+" "+one.y+" "+endp.x+" "+endp.y+" Q "+two.x+" "+two.y+" "+o.x+" "+o.y);

            gc.beginPath();
            gc.moveTo(o.x,o.y);
            gc.appendSVGPath(SVG);
            gc.closePath();
            gc.setFill(changeColor(pcolor,pchange));
            gc.fill();
            gc.setStroke(changeColor(scolor,schange));
            gc.setLineWidth(strokewidth);
            gc.stroke();
        }
    }

    /*
        传入原来的颜色和偏移量，按偏移量对颜色进行改变
     */
    private static Color changeColor(Color c,double offset) {
        double r = c.getRed()+random()*offset;
        double g = c.getGreen()+random()*offset;
        double b = c.getBlue()+random()*offset;
        if(r > 1)   r -= offset;
        if(g > 1)   g -= offset;
        if(b > 1)   b -= offset;
        return Color.color(r,g,b);
    }
}

/*
    花朵类，一个实例表示一朵花
 */
class Flower {
    private Color hcolor,scolor;    //花蕊颜色，轮廓颜色
    private double radius,swidth;   //花蕊半径，轮廓宽度
    private int layer,breedNumber;  //花瓣层数，花朵种类
    private Color cchange;          //每层花瓣颜色渐变
    private Petal[] petal = new Petal[10];  //花瓣，默认最多十层

    public Flower() {//默认黑色花
        hcolor = Color.BLACK;
        scolor = Color.BLACK;
        radius = 10;
        swidth = 1;
        layer = 1;
        breedNumber = 0;
        cchange = Color.BLACK;
        makePetal(0);
    }
    /*
        创建不同特定品种的花
     */
    public Flower(int breedNumber) {
        switch(breedNumber){
            case 1: //向日葵
                hcolor = Color.GREEN;
                scolor = Color.BROWN;
                radius = 12;
                swidth = 2;
                layer = 1;
                cchange = Color.BLACK;
                this.breedNumber = 1;
                makePetal(1);
                break;
            case 2: //随机彩虹花
                hcolor = Color.WHITE;
                scolor = Color.GOLD;
                radius = 4;
                swidth = 1;
                layer = 1;
                cchange = Color.color(random(),random(),random());
                this.breedNumber = 2;
                makePetal(2);
                break;
            case 3: //三瓣花
                hcolor = Color.WHITE;
                scolor = Color.GREEN;
                radius = 3;
                swidth = 1;
                layer = 1;
                cchange = Color.BLACK;
                this.breedNumber = 3;
                makePetal(3);
                break;
            case 4: //多层花
                hcolor = Color.PINK;
                scolor = Color.WHITE;
                radius = 5;
                swidth = 1;
                layer = 3;
                cchange = Color.color(0.7,0.5,0.8);
                this.breedNumber = 4;
                makePetal(4);
                break;
            default://默认黑色花
                hcolor = Color.BLACK;
                scolor = Color.BLACK;
                radius = 10;
                swidth = 1;
                layer = 1;
                cchange = Color.BLACK;
                this.breedNumber = 0;
                makePetal(0);
        }
    }

    /*
        生成花瓣
     */
    private void makePetal(int breedNumber) {
        if(breedNumber == 0)    petal[0] = new Petal();
        else    petal[0] = new Petal(breedNumber);
        for(int i=1;i<layer;i++){
            petal[i] = new Petal(petal[i-1],cchange);
        }
    }

    /*
        绘制花朵
        传入参数：花朵中心点位置，画布实例
     */
    public void paintFlower(MyPoint o,GraphicsContext gc) {
        for(int i=layer-1;i>=0;i--){
            petal[i].paint(o,random(),gc);
        }
        gc.setFill(hcolor);
        gc.setStroke(scolor);
        gc.setLineWidth(swidth);
        gc.fillOval(o.x-radius,o.y-radius,radius*2,radius*2);
        gc.strokeOval(o.x-radius,o.y-radius,radius*2,radius*2);
    }

    /*
        重生为新的花朵
     */
    public void birthfrom(Flower fa,Flower mo) {
        double r,g,b;
        r = (2*fa.hcolor.getRed()+mo.hcolor.getRed())/3;
        g = (2*fa.hcolor.getGreen()+mo.hcolor.getGreen())/3;
        b = (2*fa.hcolor.getBlue()+mo.hcolor.getBlue())/3;
        hcolor = Color.color(r,g,b);
        r = (2*fa.scolor.getRed()+mo.scolor.getRed())/3;
        g = (2*fa.scolor.getGreen()+mo.scolor.getGreen())/3;
        b = (2*fa.scolor.getBlue()+mo.scolor.getBlue())/3;
        scolor = Color.color(r,g,b);
        radius = (fa.radius+mo.radius)/2;
        swidth = (fa.swidth+mo.swidth)/2;
        layer = (fa.layer+mo.layer)/2;
        cchange = (random()>0.5)?fa.cchange:mo.cchange;
        breedNumber = fa.breedNumber;
        Random rand = new Random();
        if((fa.equals(mo) && (random()>0.5)) || random()>0.8) {//产生变异，自交概率更大
            int t = rand.nextInt(6);
            if(t < 1) {
                hcolor = Color.color(random(),random(),random());
            }
            else if(t < 2) {
                scolor = Color.color(random(),random(),random());
            }
            else if(t < 3) {
                radius += rand.nextInt(2)*4 - 2;
                if(radius <= 0)    radius = 2;
            }
            else if(t < 4) {
                swidth += rand.nextInt(2)*2 - 1;
                if(swidth  <= 0) swidth = 1;
            }
            else if(t < 5) {
                layer += rand.nextInt(2)*2 - 1;
                if(layer <= 0)  layer = 1;
            }
            else if(t < 6) {
                cchange = Color.color(random(),random(),random());
            }

        }
        petal[0] = new Petal(fa.petal[0],mo.petal[0]);
        for(int i=1;i<layer;i++){
            petal[i] = new Petal(petal[i-1],cchange);
        }
    }
}

/*
    植物类，包括枝条和花朵
*/
class Plant {
    private final static int MAXX = 10;
    private Flower flower = new Flower();
    private int fnum,lbnum,forks; //花朵数量，枝条数量，最大分叉数
    private double height,width;  //植物高度，枝条宽度
    private Color lbcolor;

    private int now;

    public Plant() {
        fnum = 1;
        lbnum = 1;
        forks = 0;
        height = 100;
        lbcolor = Color.BROWN;
    }
    /*
        通过参数构造植物
     */
    public Plant(int flowernum,int limbnum,int fork,double height,double width,Color lbcolor,Flower flower) {
        fnum = flowernum;
        lbnum = limbnum;
        forks = fork;
        this.height = height;
        this.width = width;
        this.lbcolor = lbcolor;
        this.flower = flower;
    }

    /*
        队列逐层绘制植物
        传入参数：植物位置点，画布实例
     */
    public void paintPlant(MyPoint locate,GraphicsContext gc) {
        int templbnum = 1;
        int tempfnum = 0;
        MyPoint[][] queue = new MyPoint[MAXX][2];
        for(int i = 0;i < MAXX;i++) {queue[i][0] = new MyPoint();queue[i][1] = new MyPoint();}
        MyPoint[] fq = new MyPoint[MAXX];
        for(int i = 0;i < MAXX;i++) fq[i] = new MyPoint();
        int fr = 0,re = 0,ft = 0;
        queue[re][0].setXandY(locate);
        queue[re][1].setX(width);
        queue[re][1].setY(height);
        re += 1;
        while(re != fr) {
            double tempx = random()*Math.sqrt(height-queue[fr][1].y)-Math.sqrt(height-queue[fr][1].y)/2;
            tempx *= 10;
            double rand = random();
            double tempy = ((rand<0.5)?0.5:rand)*(-1)*fnum/lbnum*queue[fr][1].y;
            if(tempy + queue[fr][1].y < 0)  tempy = (-1)*(queue[fr][1].y-2);
            paintLimb(queue[fr][0],new MyPoint(tempx,tempy),queue[fr][1].x,lbcolor,gc);
            int forknum = 0;
            for(int i = 0;i < forks;i++) {
                if((random() > 0.3) && (templbnum < lbnum) && ((tempfnum + forknum) < fnum)) {
                    forknum++;
                    if(forknum > 1) tempfnum++;
                    templbnum++;
                    queue[re][0].setX(queue[fr][0].x+tempx);
                    queue[re][0].setY(queue[fr][0].y+tempy);
                    queue[re][1].setX(queue[fr][1].x/2);
                    queue[re][1].setY(queue[fr][1].y+tempy);
                    re = (re + 1)%MAXX;
                }
                else if((i == forks-1) && (forknum == 0) && (templbnum < lbnum)) {
                    templbnum++;
                    queue[re][0].setX(queue[fr][0].x+tempx);
                    queue[re][0].setY(queue[fr][0].y+tempy);
                    queue[re][1].setX(queue[fr][1].x/2);
                    queue[re][1].setY(queue[fr][1].y+tempy);
                    re = (re + 1)%MAXX;
                }
            }
            if(forknum == 0) {
                fq[ft].setX(queue[fr][0].x+tempx);
                fq[ft].setY(queue[fr][0].y+tempy);
                ft++;
            }
            fr = (fr + 1)%MAXX;
        }

        now = 0;
        Timeline growing = new Timeline();
        growing.setCycleCount(ft);

        Duration duration = Duration.millis(300);
        EventHandler onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                flower.paintFlower(fq[now],gc);
                now++;
            }
        };
        KeyFrame keyframe = new KeyFrame(duration,onFinished);

        growing.getKeyFrames().add(keyframe);
        growing.play();
    }

    /*
        绘制枝条
        传入参数：起始点，终点，枝条宽度，枝条颜色，画布实例
     */
    private void paintLimb(MyPoint start,MyPoint over,double width,Color color,GraphicsContext gc) {
        MyPoint mid = new MyPoint(over.x/2+random()*over.x*0.8-0.4*over.x,over.y/2);
        String SVG = new String("Q"+(start.x+mid.x)+" "+(start.y+mid.y)+" "+(start.x+over.x)+" "+(start.y+over.y)+" L "+(start.x+over.x+width/2)+" "+(start.y+over.y)+" Q "+(start.x+mid.x+width/1.5)+" "+(start.y+mid.y)+" "+(start.x+width)+" "+start.y);
        gc.beginPath();
        gc.moveTo(start.x,start.y);
        gc.appendSVGPath(SVG);
        gc.closePath();
        gc.setFill(color);
        gc.fill();
    }

    /*
        重生为新的植物
     */
    public void birthfrom(Plant fa,Plant mo) {
        fnum = (fa.fnum+mo.fnum)/2;
        lbnum = (fa.lbnum+mo.fnum)/2;
        if(lbnum < fnum)    lbnum = fnum;
        forks = (fa.forks+mo.forks)/2;
        if(forks > fnum)    forks = fnum;
        height = (fa.height+mo.height)/2;
        width = (fa.width+mo.width)/2;
        double r,g,b;
        r = (fa.lbcolor.getRed()+mo.lbcolor.getRed())/2;
        g = (fa.lbcolor.getGreen()+mo.lbcolor.getGreen())/2;
        b = (fa.lbcolor.getBlue()+mo.lbcolor.getBlue())/2;
        lbcolor = Color.color(r,g,b);
        flower.birthfrom(fa.flower,mo.flower);
        Random rand = new Random();
        if((fa.equals(mo) && (random()>0.5)) || random()>0.8) {//产生变异，自交概率更大
            int t = rand.nextInt(6);
            if(t < 1) {
                fnum += rand.nextInt(2)*2 - 1;
                if(fnum <= 1)   fnum = 1;
            }
            else if(t < 2) {
                lbnum += rand.nextInt(2)*2 - 1;
                if(lbnum < fnum)  lbnum = fnum;
            }
            else if(t < 3) {
                forks += rand.nextInt(2)*2 - 1;
                if(forks > fnum)    forks = fnum;
                else if(forks < 1)  forks = 1;
            }
            else if(t < 4) {
                height += rand.nextInt(2)*20 - 10;
                if(height < 10) height = 10;
            }
            else if(t < 5) {
                width += rand.nextInt(2)*10 - 5;
                if(width < 3)  width = 3;
            }
            else if(t < 6) {
                lbcolor = Color.color(random(),random(),random());
            }

        }
    }
}

public class Main extends Application {

    private int selected_locate = -1;
    public Plant[] plants = new Plant[6];
    private int father = -1;
    private int mother = -1;
    private Media bgm = null;
    private MediaPlayer bgmp = null;
    private Media se = null;
    private MediaPlayer sep = null;
    private DropShadow tds = new DropShadow();
    private DropShadow fds = new DropShadow();
    private DropShadow mds = new DropShadow();
    private Canvas[] flowerlayer = new Canvas[6];
    private GraphicsContext[] fgc = new GraphicsContext[6];
    private Text rules = new Text();
    private TextFlow text = new TextFlow();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Pane pback = new Pane();
        Pane pfront = new Pane();
        Group fgroup = new Group();
        Group picture = new Group();

        MenuBar menu = new MenuBar();
        Menu storemenu = new Menu("Store");
        Menu scmenu = new Menu("PrtSc");
        Menu helpmenu = new Menu("Help");
        Menu exitmenu = new Menu("Exit");
        menu.getMenus().addAll(storemenu,scmenu,helpmenu,exitmenu);
        menu.setPrefWidth(900);
        MenuItem prtsc = new MenuItem("Screenshot");
        MenuItem help = new MenuItem("Game rules");
        MenuItem exit = new MenuItem("exit");
        scmenu.getItems().add(prtsc);
        helpmenu.getItems().add(help);
        exitmenu.getItems().add(exit);
        MenuItem[] storeflo = new MenuItem[5];
        storeflo[0] = new MenuItem("原初之花");
        storeflo[1] = new MenuItem("向日葵");
        storeflo[2] = new MenuItem("彩虹花");
        storeflo[3] = new MenuItem("三瓣花");
        storeflo[4] = new MenuItem("雪之蕊");
        for(int i = 0;i < 5;i++)    storemenu.getItems().add(storeflo[i]);

        Image backimage = new Image("file:backimage.jpg");
        ImageView back = new ImageView(backimage);
        pback.getChildren().add(back);
        Image earthimage = new Image("file:earth.png");
        ImageView earth = new ImageView(earthimage);
        pfront.getChildren().add(earth);

        init();

        /*
            鼠标事件处理
         */
        pfront.addEventHandler(MouseEvent.MOUSE_PRESSED,new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if(selected_locate != -1) {
                    flowerlayer[selected_locate].setEffect(fds);
                    father = selected_locate;
                }
            }
        });
        pfront.addEventHandler(MouseEvent.MOUSE_DRAGGED,new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MyPoint o = new MyPoint(me.getX(),me.getY());

                int locateplace = (int)(o.x-24)/142;
                if(locateplace < 0) locateplace = 0;
                else if(locateplace > 5) locateplace = 5;

                flowerlayer[locateplace].toFront();
                Image tempim = flowerlayer[locateplace].snapshot(new SnapshotParameters(),null);
                Color tc = tempim.getPixelReader().getColor((int)o.x,(int)o.y);
                if(!tc.equals(Color.WHITE)) {//鼠标进入植物范围
                    if(selected_locate != -1 && selected_locate != locateplace && selected_locate != father)//从父本植物换到另一株
                        flowerlayer[selected_locate].setEffect(null);
                    selected_locate = locateplace;
                    if(selected_locate != father)   flowerlayer[selected_locate].setEffect(mds);
                }
                else if(selected_locate != -1) {
                    if(selected_locate != father)
                        flowerlayer[selected_locate].setEffect(null);
                    selected_locate = -1;
                }
            }
        });
        pfront.addEventHandler(MouseEvent.MOUSE_RELEASED,new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if(selected_locate == -1) {
                    if(father != -1) {
                        flowerlayer[father].setEffect(null);
                        father = -1;
                    }
                }
                else if(father != -1) {
                    flowerlayer[father].setEffect(null);
                    mother = selected_locate;
                    birth();
                }
            }
        });
        pfront.addEventHandler(MouseEvent.MOUSE_MOVED,new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MyPoint o = new MyPoint(me.getX(),me.getY());

                int locateplace = (int)(o.x-24)/142;
                if(locateplace < 0) locateplace = 0;
                else if(locateplace > 5) locateplace = 5;

                flowerlayer[locateplace].toFront();


                //将画面转化为Image
                Image tempim = flowerlayer[locateplace].snapshot(new SnapshotParameters(),null);
                Color tc = tempim.getPixelReader().getColor((int)o.x,(int)o.y);
                if(!tc.equals(Color.WHITE)) {//鼠标进入植物范围
                    //从一株植物换到另一株
                    if(selected_locate != -1 && selected_locate != locateplace)
                        flowerlayer[selected_locate].setEffect(null);//消除旧的阴影
                    selected_locate = locateplace;
                    flowerlayer[selected_locate].setEffect(tds);//设置新阴影
                }
                else if(selected_locate != -1) {//鼠标离开植物范围
                    flowerlayer[selected_locate].setEffect(null);//消除阴影
                    selected_locate = -1;
                }



            }
        });
        text.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                text.setVisible(false);
            }
        });

        /*
            菜单的响应
         */
        storeflo[0].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int i = getI();
                plants[i] = new Plant(3,5,3,250,10,Color.BLACK,new Flower(0));
                plants[i].paintPlant(new MyPoint(i*142+83,520),fgc[i]);
            }
        });
        storeflo[1].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int i = getI();
                plants[i] = new Plant(1,1,1,200,8,Color.GREEN,new Flower(1));
                plants[i].paintPlant(new MyPoint(i*142+83,520),fgc[i]);
            }
        });
        storeflo[2].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int i = getI();
                plants[i] = new Plant(3,5,3,250,10,Color.PINK,new Flower(2));
                plants[i].paintPlant(new MyPoint(i*142+83,520),fgc[i]);
            }
        });
        storeflo[3].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int i = getI();
                plants[i] = new Plant(6,10,5,250,5,Color.BROWN,new Flower(3));
                plants[i].paintPlant(new MyPoint(i*142+83,520),fgc[i]);
            }
        });
        storeflo[4].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int i = getI();
                plants[i] = new Plant(2,3,2,250,10,Color.BLUEVIOLET,new Flower(4));
                plants[i].paintPlant(new MyPoint(i*142+83,520),fgc[i]);
            }
        });
        prtsc.setOnAction(new EventHandler<ActionEvent>() {//将当前画面截图保存
            @Override
            public void handle(ActionEvent event) {
                Image screenshot = picture.snapshot(new SnapshotParameters(),null);
                try {
                    Calendar t = Calendar.getInstance();
                    File file = new File("screenshot\\" + t.getTimeInMillis() + ".png");
                    ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), "png",file);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        help.setOnAction(new EventHandler<ActionEvent>() {//显示游戏规则
            @Override
            public void handle(ActionEvent event) {
                text.setVisible(true);
            }
        });
        exitmenu.setOnAction(new EventHandler<ActionEvent>() {//退出
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        /*
            构造javafx场景树
         */
        picture.getChildren().add(pback);
        for(int i = 0;i < 6;i++) {
            fgroup.getChildren().add(flowerlayer[i]);
        }
        picture.getChildren().add(fgroup);
        picture.getChildren().add(pfront);
        root.getChildren().add(picture);
        text.getChildren().add(rules);
        root.getChildren().add(text);
        root.getChildren().add(menu);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /*
        预设
     */
    public void init() {
        rules.setText("\n  欢迎进入游戏：园艺师！\n" +
                "  在这里，你将通过自交与杂交，培育出自己独特的花！\n\n" +
                "  游戏规则：\n" +
                "  [1] 你可以在Store中购买已知品种的花作为培育的基础；\n" +
                "  [2] 鼠标左键点击一株花，将会得到其自交的后代；\n" +
                "  [3] 按住一株花并拖动到另一株上，将会得到它们的杂交后代；\n\n" +
                "  注意，新植物生长的位置是随机的，如果遇到喜欢的花，可以使用Screenshot截图保存。 \n" +
                "  祝您游戏愉快！\n");
        rules.setFont(Font.font(15));
        rules.setFill(Color.BLUEVIOLET);
        text.setLayoutX(150);
        text.setLayoutY(50);
        text.setBackground(new Background(new BackgroundFill(Color.color(1,1,1,0.5),null,null)));

        for(int i = 0;i < 6;i++) {
            flowerlayer[i] = new Canvas(900,600);
            fgc[i] = flowerlayer[i].getGraphicsContext2D();
        }

        tds.setOffsetY(3.0f);
        tds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        fds.setOffsetY(3.0f);
        fds.setColor(Color.BLUE);
        mds.setOffsetY(3.0f);
        mds.setColor(Color.RED);

        String url = new File("bgm.mp3").toURI().toString();
        bgm = new Media(url);
        bgmp = new MediaPlayer(bgm);
        bgmp.setCycleCount(MediaPlayer.INDEFINITE);
        bgmp.play();
        url = new File("birth.mp3").toURI().toString();
        se = new Media(url);
        sep = new MediaPlayer(se);

        for(int i = 0;i < 6;i++) {
            plants[i] = null;
        }
    }

    /*
        诞生新的植物
     */
    private void birth() {
        Random rand = new Random();
        int tc = 0;
        for(;tc < 6;tc++) {
            if(plants[tc] == null)  break;
        }
        if(tc == 6) {
            tc = rand.nextInt(6);
            while(tc == father || tc == mother) tc = rand.nextInt(6);
        }
        else    plants[tc] = new Plant();
        plants[tc].birthfrom(plants[father],plants[mother]);
        fgc[tc].clearRect(0,0,900,600);
        sep.stop();
        sep.play();
        plants[tc].paintPlant(new MyPoint(tc*142+rand.nextInt(42)+62,520),fgc[tc]);
        father = -1;
        mother = -1;
    }

    /*
        找到播种新植物的位置
     */
    private int getI() {
        int i = 0;
        for(;i < 6;i++) {
            if(plants[i] == null)   break;
        }
        if(i == 6) {
            i = new Random().nextInt(6);
            fgc[i].clearRect(0,0,900,600);
        }
        return i;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
