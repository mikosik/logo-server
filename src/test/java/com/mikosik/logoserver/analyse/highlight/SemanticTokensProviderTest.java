package com.mikosik.logoserver.analyse.highlight;

import static com.google.common.truth.Truth.assertThat;

import com.mikosik.logoserver.analyse.parser.Parser;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SemanticTokensProviderTest {
  @Test
  void returnsTokensForForwardCommand() {
    assertSemanticTokensOf(
        "forward 10",
        List.of(
            0, 0, 7, 0, 0, // forward
            0, 8, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForBackCommand() {
    assertSemanticTokensOf(
        "back 20",
        List.of(
            0, 0, 4, 0, 0, // back
            0, 5, 2, 2, 0 // 20
            ));
  }

  @Test
  void returnsTokensForLeftCommand() {
    assertSemanticTokensOf(
        "left 30",
        List.of(
            0, 0, 4, 0, 0, // left
            0, 5, 2, 2, 0 // 30
            ));
  }

  @Test
  void returnsTokensForRightCommand() {
    assertSemanticTokensOf(
        "right 40",
        List.of(
            0, 0, 5, 0, 0, // right
            0, 6, 2, 2, 0 // 40
            ));
  }

  @Test
  void returnsTokensForShortFormCommands() {
    assertSemanticTokensOf(
        """
        fd 10
        bk 20
        lt 30
        rt 40""",
        List.of(
            0, 0, 2, 0, 0, // fd
            0, 3, 2, 2, 0, // 10
            1, 0, 2, 0, 0, // bk
            0, 3, 2, 2, 0, // 20
            1, 0, 2, 0, 0, // lt
            0, 3, 2, 2, 0, // 30
            1, 0, 2, 0, 0, // rt
            0, 3, 2, 2, 0 // 40
            ));
  }

  @Test
  void returnsTokensForMultipleCommands() {
    assertSemanticTokensOf(
        """
        forward 10
        back 20
        left 30
        right 40""",
        List.of(
            0, 0, 7, 0, 0, // forward
            0, 8, 2, 2, 0, // 10
            1, 0, 4, 0, 0, // back
            0, 5, 2, 2, 0, // 20
            1, 0, 4, 0, 0, // left
            0, 5, 2, 2, 0, // 30
            1, 0, 5, 0, 0, // right
            0, 6, 2, 2, 0 // 40
            ));
  }

  @Test
  void returnsTokensForNegativeNumbers() {
    assertSemanticTokensOf(
        "forward -10",
        List.of(
            0, 0, 7, 0, 0, // forward
            0, 8, 3, 2, 0 // -10
            ));
  }

  @Test
  void returnsTokensForDecimalNumbers() {
    assertSemanticTokensOf(
        "forward 10.5",
        List.of(
            0, 0, 7, 0, 0, // forward
            0, 8, 4, 2, 0 // 10.5
            ));
  }

  @Test
  void returnsTokensForHomeCommand() {
    assertSemanticTokensOf(
        "home",
        List.of(
            0, 0, 4, 0, 0 // home
            ));
  }

  @Test
  void returnsTokensForSetXCommand() {
    assertSemanticTokensOf(
        "setx 50",
        List.of(
            0, 0, 4, 0, 0, // setx
            0, 5, 2, 2, 0 // 50
            ));
  }

  @Test
  void returnsTokensForSetYCommand() {
    assertSemanticTokensOf(
        "sety 60",
        List.of(
            0, 0, 4, 0, 0, // sety
            0, 5, 2, 2, 0 // 60
            ));
  }

  @Test
  void returnsTokensForSetXYCommand() {
    assertSemanticTokensOf(
        "setxy 70 80",
        List.of(
            0, 0, 5, 0, 0, // setxy
            0, 6, 2, 2, 0, // 70
            0, 3, 2, 2, 0 // 80
            ));
  }

  @Test
  void returnsTokensForSetXY2Command() {
    assertSemanticTokensOf(
        "set pos [90 100]",
        List.of(
            0, 0, 3, 0, 0, // set
            0, 4, 3, 0, 0, // pos
            0, 5, 2, 2, 0, // 90
            0, 3, 3, 2, 0 // 100
            ));
  }

  @Test
  void returnsTokensForSetHeadingCommand() {
    assertSemanticTokensOf(
        "setheading 45",
        List.of(
            0, 0, 10, 0, 0, // setheading
            0, 11, 2, 2, 0 // 45
            ));
  }

  @Test
  void returnsTokensForSetHeadingShortCommand() {
    assertSemanticTokensOf(
        "seth 45",
        List.of(
            0, 0, 4, 0, 0, // seth
            0, 5, 2, 2, 0 // 45
            ));
  }

  @Test
  void returnsTokensForShowTurtleCommand() {
    assertSemanticTokensOf(
        "showturtle",
        List.of(
            0, 0, 10, 0, 0 // showturtle
            ));
  }

  @Test
  void returnsTokensForShowTurtleShortCommand() {
    assertSemanticTokensOf(
        "st",
        List.of(
            0, 0, 2, 0, 0 // st
            ));
  }

  @Test
  void returnsTokensForHideTurtleCommand() {
    assertSemanticTokensOf(
        "hideturtle",
        List.of(
            0, 0, 10, 0, 0 // hideturtle
            ));
  }

  @Test
  void returnsTokensForHideTurtleShortCommand() {
    assertSemanticTokensOf(
        "ht",
        List.of(
            0, 0, 2, 0, 0 // ht
            ));
  }

  @Test
  void returnsTokensForCleanCommand() {
    assertSemanticTokensOf(
        "clean",
        List.of(
            0, 0, 5, 0, 0 // clean
            ));
  }

  @Test
  void returnsTokensForClearScreenCommand() {
    assertSemanticTokensOf(
        "clearscreen",
        List.of(
            0, 0, 11, 0, 0 // clearscreen
            ));
  }

  @Test
  void returnsTokensForClearScreenShortCommand() {
    assertSemanticTokensOf(
        "cs",
        List.of(
            0, 0, 2, 0, 0 // cs
            ));
  }

  @Test
  void returnsTokensForPenUpCommand() {
    assertSemanticTokensOf(
        "penup",
        List.of(
            0, 0, 5, 0, 0 // penup
            ));
  }

  @Test
  void returnsTokensForPenUpShortCommand() {
    assertSemanticTokensOf(
        "pu",
        List.of(
            0, 0, 2, 0, 0 // pu
            ));
  }

  @Test
  void returnsTokensForPenDownCommand() {
    assertSemanticTokensOf(
        "pendown",
        List.of(
            0, 0, 7, 0, 0 // pendown
            ));
  }

  @Test
  void returnsTokensForPenDownShortCommand() {
    assertSemanticTokensOf(
        "pd",
        List.of(
            0, 0, 2, 0, 0 // pd
            ));
  }

  @Test
  void returnsTokensForSetColorCommand() {
    assertSemanticTokensOf(
        "setcolor 5",
        List.of(
            0, 0, 8, 0, 0, // setcolor
            0, 9, 1, 2, 0 // 5
            ));
  }

  @Test
  void returnsTokensForSetPenColorCommand() {
    assertSemanticTokensOf(
        "setpencolor 5",
        List.of(
            0, 0, 11, 0, 0, // setpencolor
            0, 12, 1, 2, 0 // 5
            ));
  }

  @Test
  void returnsTokensForSetWidthCommand() {
    assertSemanticTokensOf(
        "setwidth 3",
        List.of(
            0, 0, 8, 0, 0, // setwidth
            0, 9, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForSetPenSizeCommand() {
    assertSemanticTokensOf(
        "setpensize 3",
        List.of(
            0, 0, 10, 0, 0, // setpensize
            0, 11, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForToCommand() {
    assertSemanticTokensOf(
        """
        to square :size
          repeat 4 [forward :size right 90]
        end""",
        List.of(
            0, 0, 2, 0, 0, // to
            0, 3, 6, 3, 2, // square (FUNCTION with DEFINITION modifier)
            1, 2, 6, 0, 0, // repeat
            0, 7, 1, 2, 0, // 4
            0, 3, 7, 0, 0, // forward
            0, 8, 5, 4, 0, // :size (VARIABLE)
            0, 6, 5, 0, 0, // right
            0, 6, 2, 2, 0, // 90
            1, 0, 3, 0, 0 // end
            ));
  }

  @Test
  void returnsTokensForMakeCommand() {
    assertSemanticTokensOf(
        "make \"x 100",
        List.of(
            0, 0, 4, 0, 0, // make
            0, 5, 2, 4, 2, // "x (VARIABLE with DEFINITION modifier)
            0, 3, 3, 2, 0 // 100
            ));
  }

  @Test
  void returnsTokensForRepeatCommand() {
    assertSemanticTokensOf(
        "repeat 5 [forward 50]",
        List.of(
            0, 0, 6, 0, 0, // repeat
            0, 7, 1, 2, 0, // 5
            0, 3, 7, 0, 0, // forward
            0, 8, 2, 2, 0 // 50
            ));
  }

  @Test
  void returnsTokensForIfCommand() {
    assertSemanticTokensOf(
        "if 1 > 0 [forward 10]",
        List.of(
            0, 0, 2, 0, 0, // if
            0, 3, 1, 2, 0, // 1
            0, 4, 1, 2, 0, // 0
            0, 3, 7, 0, 0, // forward
            0, 8, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForArcCommand() {
    assertSemanticTokensOf(
        "arc 45 90",
        List.of(
            0, 0, 3, 0, 0, // arc
            0, 4, 2, 2, 0, // 45
            0, 3, 2, 2, 0 // 90
            ));
  }

  @Test
  void returnsTokensForEllipseCommand() {
    assertSemanticTokensOf(
        "ellipse 50 30",
        List.of(
            0, 0, 7, 0, 0, // ellipse
            0, 8, 2, 2, 0, // 50
            0, 3, 2, 2, 0 // 30
            ));
  }

  @Test
  void returnsTokensForFillCommand() {
    assertSemanticTokensOf(
        "fill",
        List.of(
            0, 0, 4, 0, 0 // fill
            ));
  }

  @Test
  void returnsTokensForFilledCommand() {
    assertSemanticTokensOf(
        "filled 5 [forward 10]",
        List.of(
            0, 0, 6, 0, 0, // filled
            0, 7, 1, 2, 0, // 5
            0, 3, 7, 0, 0, // forward
            0, 8, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForLabelCommand() {
    assertSemanticTokensOf(
        "label \"hello",
        List.of(
            0,
            0,
            5,
            0,
            0, // label
            0,
            6,
            6,
            1,
            0 // "hello
            ));
  }

  @Test
  void returnsTokensForPrintCommand() {
    assertSemanticTokensOf(
        "print \"hello",
        List.of(
            0,
            0,
            5,
            0,
            0, // print
            0,
            6,
            6,
            1,
            0 // "hello
            ));
  }

  @Test
  void returnsTokensForShowCommand() {
    assertSemanticTokensOf(
        "show \"hello",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            6,
            1,
            0 // "hello
            ));
  }

  @Test
  void returnsTokensForSetLabelHeightCommand() {
    assertSemanticTokensOf(
        "setlabelheight 12",
        List.of(
            0, 0, 14, 0, 0, // setlabelheight
            0, 15, 2, 2, 0 // 12
            ));
  }

  @Test
  void returnsTokensForWrapCommand() {
    assertSemanticTokensOf(
        "wrap",
        List.of(
            0, 0, 4, 0, 0 // wrap
            ));
  }

  @Test
  void returnsTokensForWindowCommand() {
    assertSemanticTokensOf(
        "window",
        List.of(
            0, 0, 6, 0, 0 // window
            ));
  }

  @Test
  void returnsTokensForFenceCommand() {
    assertSemanticTokensOf(
        "fence",
        List.of(
            0, 0, 5, 0, 0 // fence
            ));
  }

  @Test
  void returnsTokensForChangeShapeCommand() {
    assertSemanticTokensOf(
        "changeshape 2",
        List.of(
            0, 0, 11, 0, 0, // changeshape
            0, 12, 1, 2, 0 // 2
            ));
  }

  @Test
  void returnsTokensForChangeShapeShortCommand() {
    assertSemanticTokensOf(
        "csh 2",
        List.of(
            0, 0, 3, 0, 0, // csh
            0, 4, 1, 2, 0 // 2
            ));
  }

  @Test
  void returnsTokensForDefineCommand() {
    assertSemanticTokensOf(
        """
        define "square [[size] [repeat 4 [forward :size right 90]]]
        end""",
        List.of(
            0, 0, 6, 0, 0, // define
            0, 7, 7, 3, 2, // "square
            0, 10, 4, 4, 1, // size
            0, 7, 6, 0, 0, // repeat
            0, 7, 1, 2, 0, // 4
            0, 3, 7, 0, 0, // forward
            0, 8, 5, 4, 0, // :size
            0, 6, 5, 0, 0, // right
            0, 6, 2, 2, 0, // 90
            1, 0, 3, 0, 0 // end
            ));
  }

  @Test
  void returnsTokensForNameCommand() {
    assertSemanticTokensOf(
        "name 100 \"x",
        List.of(
            0,
            0,
            4,
            0,
            0, // name
            0,
            5,
            3,
            2,
            0, // 100
            0,
            4,
            2,
            4,
            2 // "x (VARIABLE with DEFINITION modifier)
            ));
  }

  @Test
  void returnsTokensForLocalmakeCommand() {
    assertSemanticTokensOf(
        "localmake \"y 200",
        List.of(
            0, 0, 9, 0, 0, // localmake
            0, 10, 2, 4, 2, // "y (VARIABLE with DEFINITION modifier)
            0, 3, 3, 2, 0 // 200
            ));
  }

  @Test
  void returnsTokensForForCommand() {
    assertSemanticTokensOf(
        "for [i 1 5] [forward :i]",
        List.of(
            0, 0, 3, 0, 0, // for
            0, 5, 1, 4, 1, // i (VARIABLE with DECLARATION modifier)
            0, 2, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 5
            0, 4, 7, 0, 0, // forward
            0, 8, 2, 4, 0 // :i
            ));
  }

  @Test
  void returnsTokensForRepcountCommand() {
    assertSemanticTokensOf(
        "repcount",
        List.of(
            0, 0, 8, 0, 0 // repcount
            ));
  }

  @Test
  void returnsTokensForIfelseCommand() {
    assertSemanticTokensOf(
        "ifelse 1 > 0 [forward 10] [back 20]",
        List.of(
            0, 0, 6, 0, 0, // ifelse
            0, 7, 1, 2, 0, // 1
            0, 4, 1, 2, 0, // 0
            0, 3, 7, 0, 0, // forward
            0, 8, 2, 2, 0, // 10
            0, 5, 4, 0, 0, // back
            0, 5, 2, 2, 0 // 20
            ));
  }

  @Test
  void returnsTokensForTestCommand() {
    assertSemanticTokensOf(
        "test 1 > 0",
        List.of(
            0, 0, 4, 0, 0, // test
            0, 5, 1, 2, 0, // 1
            0, 4, 1, 2, 0 // 0
            ));
  }

  @Test
  void returnsTokensForIftrueCommand() {
    assertSemanticTokensOf(
        "iftrue [forward 10]",
        List.of(
            0, 0, 6, 0, 0, // iftrue
            0, 8, 7, 0, 0, // forward
            0, 8, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForIffalseCommand() {
    assertSemanticTokensOf(
        "iffalse [back 20]",
        List.of(
            0, 0, 7, 0, 0, // iffalse
            0, 9, 4, 0, 0, // back
            0, 5, 2, 2, 0 // 20
            ));
  }

  @Test
  void returnsTokensForWaitCommand() {
    assertSemanticTokensOf(
        "wait 5",
        List.of(
            0, 0, 4, 0, 0, // wait
            0, 5, 1, 2, 0 // 5
            ));
  }

  @Test
  void returnsTokensForByeCommand() {
    assertSemanticTokensOf(
        "bye",
        List.of(
            0, 0, 3, 0, 0 // bye
            ));
  }

  @Test
  void returnsTokensForDotimesCommand() {
    assertSemanticTokensOf(
        "dotimes [i 5] [forward :i]",
        List.of(
            0,
            0,
            7,
            0,
            0, // dotimes
            0,
            9,
            1,
            4,
            2, // i (VARIABLE with DEFINITION modifier)
            0,
            2,
            1,
            2,
            0, // 5
            0,
            4,
            7,
            0,
            0, // forward
            0,
            8,
            2,
            4,
            0 // :i (VARIABLE)
            ));
  }

  @Test
  void returnsTokensForDowhileCommand() {
    assertSemanticTokensOf(
        "do.while [forward 10] 1 > 0",
        List.of(
            0, 0, 8, 0, 0, // do.while
            0, 10, 7, 0, 0, // forward
            0, 8, 2, 2, 0, // 10
            0, 4, 1, 2, 0, // 1
            0, 4, 1, 2, 0 // 0
            ));
  }

  @Test
  void returnsTokensForWhileCommand() {
    assertSemanticTokensOf(
        "while 1 > 0 [forward 10]",
        List.of(
            0, 0, 5, 0, 0, // while
            0, 6, 1, 2, 0, // 1
            0, 4, 1, 2, 0, // 0
            0, 3, 7, 0, 0, // forward
            0, 8, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForDountilCommand() {
    assertSemanticTokensOf(
        "do.until [forward 10] 1 > 0",
        List.of(
            0, 0, 8, 0, 0, // do.until
            0, 10, 7, 0, 0, // forward
            0, 8, 2, 2, 0, // 10
            0, 4, 1, 2, 0, // 1
            0, 4, 1, 2, 0 // 0
            ));
  }

  @Test
  void returnsTokensForUntilCommand() {
    assertSemanticTokensOf(
        "until 1 > 0 [forward 10]",
        List.of(
            0, 0, 5, 0, 0, // until
            0, 6, 1, 2, 0, // 1
            0, 4, 1, 2, 0, // 0
            0, 3, 7, 0, 0, // forward
            0, 8, 2, 2, 0 // 10
            ));
  }

  // Queries
  @Test
  void returnsTokensForPosCommand() {
    assertSemanticTokensOf(
        "show pos",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 3, 0, 0 // pos
            ));
  }

  @Test
  void returnsTokensForXcorCommand() {
    assertSemanticTokensOf(
        "show xcor",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 4, 0, 0 // xcor
            ));
  }

  @Test
  void returnsTokensForYcorCommand() {
    assertSemanticTokensOf(
        "show ycor",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 4, 0, 0 // ycor
            ));
  }

  @Test
  void returnsTokensForHeadingCommand() {
    assertSemanticTokensOf(
        "show heading",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            7,
            0,
            0 // heading
            ));
  }

  @Test
  void returnsTokensForTowardsCommand() {
    assertSemanticTokensOf(
        "show towards",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            7,
            0,
            0 // towards
            ));
  }

  @Test
  void returnsTokensForShownpCommand() {
    assertSemanticTokensOf(
        "show shownp",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            6,
            0,
            0 // shownp
            ));
  }

  @Test
  void returnsTokensForShownpAltCommand() {
    assertSemanticTokensOf(
        "show shown?",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            6,
            0,
            0 // shown?
            ));
  }

  @Test
  void returnsTokensForLabelsizeCommand() {
    assertSemanticTokensOf(
        "show labelsize",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            9,
            0,
            0 // labelsize
            ));
  }

  @Test
  void returnsTokensForPendownpCommand() {
    assertSemanticTokensOf(
        "show pendownp",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            8,
            0,
            0 // pendownp
            ));
  }

  @Test
  void returnsTokensForPendownpAltCommand() {
    assertSemanticTokensOf(
        "show pendown?",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            8,
            0,
            0 // pendown?
            ));
  }

  @Test
  void returnsTokensForPencolorCommand() {
    assertSemanticTokensOf(
        "show pencolor",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            8,
            0,
            0 // pencolor
            ));
  }

  @Test
  void returnsTokensForPencolorShortCommand() {
    assertSemanticTokensOf(
        "show pc",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 2, 0, 0 // pc
            ));
  }

  @Test
  void returnsTokensForPensizeCommand() {
    assertSemanticTokensOf(
        "show pensize",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            7,
            0,
            0 // pensize
            ));
  }

  @Test
  void returnsTokensForDefCommand() {
    assertSemanticTokensOf(
        "show def \"square",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            3,
            0,
            0, // def
            0,
            4,
            7,
            1,
            0 // "square
            ));
  }

  @Test
  void returnsTokensForThingCommand() {
    assertSemanticTokensOf(
        "show thing \"x",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 5, 0, 0, // thing
            0, 6, 2, 1, 0 // "x
            ));
  }

  @Test
  void returnsTokensForThingshortCommand() {
    assertSemanticTokensOf(
        "show :x",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 2, 4, 0 // :x
            ));
  }

  // List operations
  @Test
  void returnsTokensForListCommand() {
    assertSemanticTokensOf(
        "show (list 1 2 3)",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 6, 4, 0, 0, // list
            0, 5, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForFirstCommand() {
    assertSemanticTokensOf(
        "show first [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 5, 0, 0, // first
            0, 7, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForButfirstCommand() {
    assertSemanticTokensOf(
        "show butfirst [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 8, 0, 0, // butfirst
            0, 10, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForLastCommand() {
    assertSemanticTokensOf(
        "show last [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 4, 0, 0, // last
            0, 6, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForButlastCommand() {
    assertSemanticTokensOf(
        "show butlast [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 7, 0, 0, // butlast
            0, 9, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForItemCommand() {
    assertSemanticTokensOf(
        "show item 2 [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 4, 0, 0, // item
            0, 5, 1, 2, 0, // 2
            0, 3, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForPickCommand() {
    assertSemanticTokensOf(
        "show pick [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 4, 0, 0, // pick
            0, 6, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  // Math operations
  @Test
  void returnsTokensForSumCommand() {
    assertSemanticTokensOf(
        "show sum 10 20",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 3, 0, 0, // sum
            0, 4, 2, 2, 0, // 10
            0, 3, 2, 2, 0 // 20
            ));
  }

  @Test
  void returnsTokensForMinusCommand() {
    assertSemanticTokensOf(
        "show minus 20 10",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 5, 0, 0, // minus
            0, 6, 2, 2, 0, // 20
            0, 3, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForRandomCommand() {
    assertSemanticTokensOf(
        "show random 1 10",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 6, 0, 0, // random
            0, 7, 1, 2, 0, // 1
            0, 2, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForModuloCommand() {
    assertSemanticTokensOf(
        "show modulo 10 3",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 6, 0, 0, // modulo
            0, 7, 2, 2, 0, // 10
            0, 3, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForPowerCommand() {
    assertSemanticTokensOf(
        "show power 2 3",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 5, 0, 0, // power
            0, 6, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  // Receivers
  @Test
  void returnsTokensForReadwordCommand() {
    assertSemanticTokensOf(
        "show (readword [prompt])",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            6,
            8,
            0,
            0 // readword
            ));
  }

  @Test
  void returnsTokensForReadlistCommand() {
    assertSemanticTokensOf(
        "show (readlist [prompt])",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            6,
            8,
            0,
            0 // readlist
            ));
  }

  // Predicates
  @Test
  void returnsTokensForWordpCommand() {
    assertSemanticTokensOf(
        "show wordp \"hello",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            5,
            0,
            0, // wordp
            0,
            6,
            6,
            1,
            0 // "hello
            ));
  }

  @Test
  void returnsTokensForWordpAltCommand() {
    assertSemanticTokensOf(
        "show word? \"hello",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            5,
            0,
            0, // word?
            0,
            6,
            6,
            1,
            0 // "hello
            ));
  }

  @Test
  void returnsTokensForListpCommand() {
    assertSemanticTokensOf(
        "show listp [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 5, 0, 0, // listp
            0, 7, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForListpAltCommand() {
    assertSemanticTokensOf(
        "show list? [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 5, 0, 0, // list?
            0, 7, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForArraypCommand() {
    assertSemanticTokensOf(
        "show arrayp [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 6, 0, 0, // arrayp
            0, 8, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForArraypAltCommand() {
    assertSemanticTokensOf(
        "show array? [1 2 3]",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 6, 0, 0, // array?
            0, 8, 1, 2, 0, // 1
            0, 2, 1, 2, 0, // 2
            0, 2, 1, 2, 0 // 3
            ));
  }

  @Test
  void returnsTokensForNumberpCommand() {
    assertSemanticTokensOf(
        "show numberp 123",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 7, 0, 0, // numberp
            0, 8, 3, 2, 0 // 123
            ));
  }

  @Test
  void returnsTokensForNumberpAltCommand() {
    assertSemanticTokensOf(
        "show number? 123",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 7, 0, 0, // number?
            0, 8, 3, 2, 0 // 123
            ));
  }

  @Test
  void returnsTokensForEmptypCommand() {
    assertSemanticTokensOf(
        "show emptyp []",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            6,
            0,
            0 // emptyp
            ));
  }

  @Test
  void returnsTokensForEmptypAltCommand() {
    assertSemanticTokensOf(
        "show empty? []",
        List.of(
            0,
            0,
            4,
            0,
            0, // show
            0,
            5,
            6,
            0,
            0 // empty?
            ));
  }

  @Test
  void returnsTokensForEqualpCommand() {
    assertSemanticTokensOf(
        "show equalp 10 10",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 6, 0, 0, // equalp
            0, 7, 2, 2, 0, // 10
            0, 3, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForEqualpAltCommand() {
    assertSemanticTokensOf(
        "show equal? 10 10",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 6, 0, 0, // equal?
            0, 7, 2, 2, 0, // 10
            0, 3, 2, 2, 0 // 10
            ));
  }

  @Test
  void returnsTokensForNotequalpCommand() {
    assertSemanticTokensOf(
        "show notequalp 10 20",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 9, 0, 0, // notequalp
            0, 10, 2, 2, 0, // 10
            0, 3, 2, 2, 0 // 20
            ));
  }

  @Test
  void returnsTokensForNotequalpAltCommand() {
    assertSemanticTokensOf(
        "show notequal? 10 20",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 9, 0, 0, // notequal?
            0, 10, 2, 2, 0, // 10
            0, 3, 2, 2, 0 // 20
            ));
  }

  @Test
  void returnsTokensForBeforepCommand() {
    assertSemanticTokensOf(
        "show beforep \"a \"b",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 7, 0, 0, // beforep
            0, 8, 2, 1, 0, // "a
            0, 3, 2, 1, 0 // "b
            ));
  }

  @Test
  void returnsTokensForBeforepAltCommand() {
    assertSemanticTokensOf(
        "show before? \"a \"b",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 7, 0, 0, // before?
            0, 8, 2, 1, 0, // "a
            0, 3, 2, 1, 0 // "b
            ));
  }

  @Test
  void returnsTokensForSubstringpCommand() {
    assertSemanticTokensOf(
        "show substringp \"a \"abc",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 10, 0, 0, // substringp
            0, 11, 2, 1, 0, // "a
            0, 3, 4, 1, 0 // "abc
            ));
  }

  @Test
  void returnsTokensForSubstringpAltCommand() {
    assertSemanticTokensOf(
        "show substring? \"a \"abc",
        List.of(
            0, 0, 4, 0, 0, // show
            0, 5, 10, 0, 0, // substring?
            0, 11, 2, 1, 0, // "a
            0, 3, 4, 1, 0 // "abc
            ));
  }

  private static void assertSemanticTokensOf(String document, List<Integer> expected) {
    var parseTree = new Parser().parse(document).parseTree();
    var data = new SemanticTokensProvider().semanticTokensOf(parseTree);
    assertThat(data).isEqualTo(expected);
  }
}
