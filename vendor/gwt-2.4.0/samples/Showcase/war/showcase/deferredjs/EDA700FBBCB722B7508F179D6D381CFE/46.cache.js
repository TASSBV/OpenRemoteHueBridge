function PEb(a){var b,c;b=tgb(a.a.Vd(Icd),138);if(b==null){c=jgb(jzb,{124:1,135:1,138:1},1,[V8c,W8c,j9c,k9c]);a.a.Xd(Icd,c);return c}else{return b}}
function QEb(a){var b,c;b=tgb(a.a.Vd(Jcd),138);if(b==null){c=jgb(jzb,{124:1,135:1,138:1},1,[Q9c,R9c,S9c,T9c,U9c,V9c]);a.a.Xd(Jcd,c);return c}else{return b}}
function Z1b(a){var b,c,d,e,f,g,i;i=new eKc;bKc(i,new Hsc('<b>Select your favorite color:<\/b>'));c=PEb(a.a);for(d=0;d<c.length;++d){b=c[d];e=new $Cc(Y4c,b);Ync(e,'cwRadioButton-color-'+b);d==2&&(e.c.disabled=true,oe(e,xe(e.Q)+l5c,true));bKc(i,e)}bKc(i,new Hsc('<br><b>Select your favorite sport:<\/b>'));g=QEb(a.a);for(d=0;d<g.length;++d){f=g[d];e=new $Cc('sport',f);Ync(e,'cwRadioButton-sport-'+rTc(f,T2c,x2c));d==2&&Znc(e,(HRc(),HRc(),GRc));bKc(i,e)}return i}
var Icd='cwRadioButtonColors',Jcd='cwRadioButtonSports';_=b2b.prototype;_.ac=function f2b(){jDb(this.b,Z1b(this.a))};v2c(tj)(46);