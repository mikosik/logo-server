package com.mikosik.logoserver.analyse.highlight;

import static com.mikosik.logoserver.analyse.base.TokenModifier.DECLARATION;
import static com.mikosik.logoserver.analyse.base.TokenModifier.DEFINITION;
import static com.mikosik.logoserver.analyse.base.TokenType.FUNCTION;
import static com.mikosik.logoserver.analyse.base.TokenType.KEYWORD;
import static com.mikosik.logoserver.analyse.base.TokenType.NUMBER;
import static com.mikosik.logoserver.analyse.base.TokenType.STRING;
import static com.mikosik.logoserver.analyse.base.TokenType.VARIABLE;

import com.google.common.collect.ImmutableList;
import com.mikosik.logoserver.analyse.base.TokenModifier;
import com.mikosik.logoserver.analyse.base.TokenType;
import com.mikosik.logoserver.analyse.parser.antlr.LogoBaseListener;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.*;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.BackContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.ForwardContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.LeftContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.RightContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Provider of semantic tokens from a given parse tree.
 * @see SemanticTokensMarshaller
 */
public class SemanticTokensProvider {
  public ImmutableList<Integer> semanticTokensOf(ParseTree parseTree) {
    var listener = new Listener();
    new ParseTreeWalker().walk(listener, parseTree);
    return listener.build();
  }

  private static class Listener extends LogoBaseListener {
    private final SemanticTokensMarshaller marshaller = new SemanticTokensMarshaller();

    @Override
    public void enterPrimaryExpr(PrimaryExprContext primaryExprContext) {
      if (primaryExprContext.NUMBER() != null) {
        addNumber(primaryExprContext.NUMBER());
      }
      if (primaryExprContext.WORD() != null) {
        add(primaryExprContext.WORD().getSymbol(), STRING);
      }
    }

    @Override
    public void enterCall(CallContext callContext) {
      add(callContext.NAME().getSymbol(), FUNCTION);
    }

    @Override
    public void enterForward(ForwardContext forwardContext) {
      addKeyword(forwardContext.cmd);
    }

    @Override
    public void enterBack(BackContext backContext) {
      addKeyword(backContext.cmd);
    }

    @Override
    public void enterLeft(LeftContext leftContext) {
      addKeyword(leftContext.cmd);
    }

    @Override
    public void enterRight(RightContext rightContext) {
      addKeyword(rightContext.cmd);
    }

    @Override
    public void enterHome(HomeContext homeContext) {
      addKeyword(homeContext.cmd);
    }

    @Override
    public void enterSetx(SetxContext setxContext) {
      addKeyword(setxContext.cmd);
    }

    @Override
    public void enterSety(SetyContext setyContext) {
      addKeyword(setyContext.cmd);
    }

    @Override
    public void enterSetxy(SetxyContext setxyContext) {
      addKeyword(setxyContext.cmd);
    }

    @Override
    public void enterSetxy2(Setxy2Context setxy2Context) {
      addKeyword(setxy2Context.cmd);
      addKeyword(setxy2Context.subcmd);
    }

    @Override
    public void enterSetheading(SetheadingContext setheadingContext) {
      addKeyword(setheadingContext.cmd);
    }

    @Override
    public void enterArc(ArcContext arcContext) {
      addKeyword(arcContext.cmd);
    }

    @Override
    public void enterEllipse(EllipseContext ellipseContext) {
      addKeyword(ellipseContext.cmd);
    }

    @Override
    public void enterShowturtle(ShowturtleContext showturtleContext) {
      addKeyword(showturtleContext.cmd);
    }

    @Override
    public void enterHideturtle(HideturtleContext hideturtleContext) {
      addKeyword(hideturtleContext.cmd);
    }

    @Override
    public void enterClean(CleanContext cleanContext) {
      addKeyword(cleanContext.cmd);
    }

    @Override
    public void enterClearscreen(ClearscreenContext clearscreenContext) {
      addKeyword(clearscreenContext.cmd);
    }

    @Override
    public void enterFill(FillContext fillContext) {
      addKeyword(fillContext.cmd);
    }

    @Override
    public void enterFilled(FilledContext filledContext) {
      addKeyword(filledContext.cmd);
    }

    @Override
    public void enterLabel(LabelContext labelContext) {
      addKeyword(labelContext.cmd);
    }

    @Override
    public void enterSetlabelheight(SetlabelheightContext setlabelheightContext) {
      addKeyword(setlabelheightContext.cmd);
    }

    @Override
    public void enterWrap(WrapContext wrapContext) {
      addKeyword(wrapContext.cmd);
    }

    @Override
    public void enterWindow(WindowContext windowContext) {
      addKeyword(windowContext.cmd);
    }

    @Override
    public void enterFence(FenceContext fenceContext) {
      addKeyword(fenceContext.cmd);
    }

    @Override
    public void enterPenup(PenupContext penupContext) {
      addKeyword(penupContext.cmd);
    }

    @Override
    public void enterPendown(PendownContext pendownContext) {
      addKeyword(pendownContext.cmd);
    }

    @Override
    public void enterSetcolor(SetcolorContext setcolorContext) {
      addKeyword(setcolorContext.cmd);
    }

    @Override
    public void enterSetwidth(SetwidthContext setwidthContext) {
      addKeyword(setwidthContext.cmd);
    }

    @Override
    public void enterChangeshape(ChangeshapeContext changeshapeContext) {
      addKeyword(changeshapeContext.cmd);
    }

    @Override
    public void enterTo(ToContext toContext) {
      addKeyword(toContext.cmd);
      add(toContext.NAME().getSymbol(), FUNCTION, DEFINITION);
    }

    @Override
    public void exitTo(ToContext toContext) {
      addKeyword(toContext.end);
    }

    @Override
    public void enterDefine(DefineContext defineContext) {
      addKeyword(defineContext.cmd);
      add(defineContext.procname, FUNCTION, DEFINITION);
      defineContext.inputs.forEach(i -> add(i, VARIABLE, DECLARATION));
    }

    @Override
    public void exitDefine(DefineContext defineContext) {
      addKeyword(defineContext.end);
    }

    @Override
    public void enterMakevar(MakevarContext makevarContext) {
      addKeyword(makevarContext.cmd);
      add(makevarContext.WORD().getSymbol(), VARIABLE, DEFINITION);
    }

    @Override
    public void enterNamevar(NamevarContext namevarContext) {
      addKeyword(namevarContext.cmd);
    }

    @Override
    public void exitNamevar(NamevarContext namevarContext) {
      add(namevarContext.WORD().getSymbol(), VARIABLE, DEFINITION);
    }

    @Override
    public void enterLocalmake(LocalmakeContext localmakeContext) {
      addKeyword(localmakeContext.cmd);
      add(localmakeContext.WORD().getSymbol(), VARIABLE, DEFINITION);
    }

    @Override
    public void enterRepeat(RepeatContext repeatContext) {
      addKeyword(repeatContext.cmd);
    }

    @Override
    public void enterFor(ForContext forContext) {
      addKeyword(forContext.cmd);
      add(forContext.localvar, VARIABLE, DECLARATION);
    }

    @Override
    public void enterRepcount(RepcountContext repcountContext) {
      addKeyword(repcountContext.cmd);
    }

    @Override
    public void enterIf(IfContext ifContext) {
      addKeyword(ifContext.cmd);
    }

    @Override
    public void enterIfelse(IfelseContext ifelseContext) {
      addKeyword(ifelseContext.cmd);
    }

    @Override
    public void enterTest(TestContext testContext) {
      addKeyword(testContext.cmd);
    }

    @Override
    public void enterIftrue(IftrueContext iftrueContext) {
      addKeyword(iftrueContext.cmd);
    }

    @Override
    public void enterIffalse(IffalseContext iffalseContext) {
      addKeyword(iffalseContext.cmd);
    }

    @Override
    public void enterWaitx(WaitxContext waitxContext) {
      addKeyword(waitxContext.cmd);
    }

    @Override
    public void enterBye(ByeContext byeContext) {
      addKeyword(byeContext.cmd);
    }

    @Override
    public void enterDotimes(DotimesContext dotimesContext) {
      addKeyword(dotimesContext.cmd);
      add(dotimesContext.NAME().getSymbol(), VARIABLE, DEFINITION);
    }

    @Override
    public void enterDowhile(DowhileContext dowhileContext) {
      addKeyword(dowhileContext.cmd);
    }

    @Override
    public void enterWhile(WhileContext whileContext) {
      addKeyword(whileContext.cmd);
    }

    @Override
    public void enterDountil(DountilContext dountilContext) {
      addKeyword(dountilContext.cmd);
    }

    @Override
    public void enterUntil(UntilContext untilContext) {
      addKeyword(untilContext.cmd);
    }

    @Override
    public void enterList(ListContext listContext) {
      addKeyword(listContext.cmd);
    }

    @Override
    public void enterPos(PosContext posContext) {
      addKeyword(posContext.cmd);
    }

    @Override
    public void enterXcor(XcorContext xcorContext) {
      addKeyword(xcorContext.cmd);
    }

    @Override
    public void enterYcor(YcorContext ycorContext) {
      addKeyword(ycorContext.cmd);
    }

    @Override
    public void enterHeading(HeadingContext headingContext) {
      addKeyword(headingContext.cmd);
    }

    @Override
    public void enterTowards(TowardsContext towardsContext) {
      addKeyword(towardsContext.cmd);
    }

    @Override
    public void enterShownp(ShownpContext shownpContext) {
      addKeyword(shownpContext.cmd);
    }

    @Override
    public void enterLabelsize(LabelsizeContext labelsizeContext) {
      addKeyword(labelsizeContext.cmd);
    }

    @Override
    public void enterPendownp(PendownpContext pendownpContext) {
      addKeyword(pendownpContext.cmd);
    }

    @Override
    public void enterPencolor(PencolorContext pencolorContext) {
      addKeyword(pencolorContext.cmd);
    }

    @Override
    public void enterPensize(PensizeContext pensizeContext) {
      addKeyword(pensizeContext.cmd);
    }

    @Override
    public void enterDef(DefContext defContext) {
      addKeyword(defContext.cmd);
    }

    @Override
    public void enterThing(ThingContext thingContext) {
      addKeyword(thingContext.cmd);
    }

    @Override
    public void enterThingshort(ThingshortContext thingshortContext) {
      add(thingshortContext.COLON_NAME().getSymbol(), VARIABLE);
    }

    @Override
    public void enterFirst(FirstContext firstContext) {
      addKeyword(firstContext.cmd);
    }

    @Override
    public void enterButfirst(ButfirstContext butfirstContext) {
      addKeyword(butfirstContext.cmd);
    }

    @Override
    public void enterLast(LastContext lastContext) {
      addKeyword(lastContext.cmd);
    }

    @Override
    public void enterButlast(ButlastContext butlastContext) {
      addKeyword(butlastContext.cmd);
    }

    @Override
    public void enterItem(ItemContext itemContext) {
      addKeyword(itemContext.cmd);
    }

    @Override
    public void enterPick(PickContext pickContext) {
      addKeyword(pickContext.cmd);
    }

    @Override
    public void enterSum(SumContext sumContext) {
      addKeyword(sumContext.cmd);
    }

    @Override
    public void enterMinus(MinusContext minusContext) {
      addKeyword(minusContext.cmd);
    }

    @Override
    public void enterRandom(RandomContext randomContext) {
      addKeyword(randomContext.cmd);
    }

    @Override
    public void enterModulo(ModuloContext moduloContext) {
      addKeyword(moduloContext.cmd);
    }

    @Override
    public void enterPower(PowerContext powerContext) {
      addKeyword(powerContext.cmd);
    }

    @Override
    public void enterReadword(ReadwordContext readwordContext) {
      addKeyword(readwordContext.cmd);
    }

    @Override
    public void enterReadlist(ReadlistContext readlistContext) {
      addKeyword(readlistContext.cmd);
    }

    @Override
    public void enterWordp(WordpContext wordpContext) {
      addKeyword(wordpContext.cmd);
    }

    @Override
    public void enterListp(ListpContext listpContext) {
      addKeyword(listpContext.cmd);
    }

    @Override
    public void enterArrayp(ArraypContext arraypContext) {
      addKeyword(arraypContext.cmd);
    }

    @Override
    public void enterNumberp(NumberpContext numberpContext) {
      addKeyword(numberpContext.cmd);
    }

    @Override
    public void enterEmptyp(EmptypContext emptypContext) {
      addKeyword(emptypContext.cmd);
    }

    @Override
    public void enterEqualp(EqualpContext equalpContext) {
      addKeyword(equalpContext.cmd);
    }

    @Override
    public void enterNotequalp(NotequalpContext notequalpContext) {
      addKeyword(notequalpContext.cmd);
    }

    @Override
    public void enterBeforep(BeforepContext beforepContext) {
      addKeyword(beforepContext.cmd);
    }

    @Override
    public void enterSubstringp(SubstringpContext substringpContext) {
      addKeyword(substringpContext.cmd);
    }

    public ImmutableList<Integer> build() {
      return marshaller.build();
    }

    private void addKeyword(Token cmd) {
      add(cmd, KEYWORD);
    }

    private void addNumber(TerminalNode number) {
      add(number.getSymbol(), NUMBER);
    }

    private void add(Token token, TokenType tokenType, TokenModifier... modifiers) {
      marshaller.add(
          token.getLine() - 1,
          token.getCharPositionInLine(),
          token.getText().length(),
          tokenType,
          modifiers);
    }
  }
}
