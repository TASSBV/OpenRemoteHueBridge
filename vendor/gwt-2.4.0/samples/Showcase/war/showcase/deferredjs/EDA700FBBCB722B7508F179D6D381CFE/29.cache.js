function quc(){}
function Muc(){}
function Luc(){}
function Ouc(){}
function Suc(){}
function Ruc(){}
function Euc(a,b){a.b=b}
function Fuc(a){if(a==uuc){return true}CA();return a==xuc}
function Guc(a){if(a==tuc){return true}CA();return a==suc}
function Puc(a){this.b=(Mwc(),Hwc).a;this.d=(Vwc(),Uwc).a;this.a=a}
function Huc(){yuc();Unc.call(this);this.b=(Mwc(),Hwc);this.c=(Vwc(),Uwc);this.e[H6c]=0;this.e[I6c]=0}
function Cuc(a,b){var c;c=tgb(a.O,88);c.b=b.a;!!c.c&&(c.c[F6c]=b.a,undefined)}
function Duc(a,b){var c;c=tgb(a.O,88);c.d=b.a;!!c.c&&hkc(c.c,G6c,b.a)}
function yuc(){yuc=d2c;ruc=new Muc;uuc=new Muc;tuc=new Muc;suc=new Muc;vuc=new Muc;wuc=new Muc;xuc=new Muc}
function zuc(a,b,c){var d;if(c==ruc){if(b==a.a){return}else if(a.a){throw new tSc('Only one CENTER widget may be added')}}Qe(b);jKc(a.j,b);c==ruc&&(a.a=b);d=new Puc(c);b.O=d;Cuc(b,a.b);Duc(b,a.c);Buc(a);Se(b,a)}
function Auc(a,b){var c,d,e,f,g,i,j;CJc(a.Q,x2c,b);i=new g_c;j=new uKc(a.j);while(j.a<j.b.c-1){c=tKc(j);g=tgb(c.O,88).a;e=tgb(i.Vd(g),132);d=!e?1:e.a;f=g==vuc?'north'+d:g==wuc?'south'+d:g==xuc?'west'+d:g==suc?'east'+d:g==uuc?'linestart'+d:g==tuc?'lineend'+d:y6c;CJc(Gl(c.Q),b,f);i.Xd(g,LSc(d+1))}}
function Buc(a){var b,c,d,e,f,g,i,j,k,n,o,p,q,r,s,t;b=a.d;while(Clc(b)>0){ol(b,Blc(b,0))}q=1;e=1;for(i=new uKc(a.j);i.a<i.b.c-1;){d=tKc(i);f=tgb(d.O,88).a;f==vuc||f==wuc?++q:(f==suc||f==xuc||f==uuc||f==tuc)&&++e}r=igb(_yb,{124:1,135:1},89,q,0);for(g=0;g<q;++g){r[g]=new Suc;r[g].b=$doc.createElement(D6c);kl(b,kCc(r[g].b))}k=0;n=e-1;o=0;s=q-1;c=null;for(i=new uKc(a.j);i.a<i.b.c-1;){d=tKc(i);j=tgb(d.O,88);t=$doc.createElement(E6c);j.c=t;j.c[F6c]=j.b;hkc(j.c,G6c,j.d);j.c[A2c]=x2c;j.c[y2c]=x2c;if(j.a==vuc){akc(r[o].b,t,r[o].a);kl(t,kCc(d.Q));t[Z8c]=n-k+1;++o}else if(j.a==wuc){akc(r[s].b,t,r[s].a);kl(t,kCc(d.Q));t[Z8c]=n-k+1;--s}else if(j.a==ruc){c=t}else if(Fuc(j.a)){p=r[o];akc(p.b,t,p.a++);kl(t,kCc(d.Q));t[_bd]=s-o+1;++k}else if(Guc(j.a)){p=r[o];akc(p.b,t,p.a);kl(t,kCc(d.Q));t[_bd]=s-o+1;--n}}if(a.a){p=r[o];akc(p.b,c,p.a);kl(c,kCc(a.a.Q))}}
_=$Yb.prototype;_.ac=function cZb(){var a,b,c;jDb(this.a,(b=new Huc,b.Q[z2c]='cw-DockPanel',b.e[H6c]=4,Euc(b,(Mwc(),Gwc)),zuc(b,new Hsc('This is the first north component'),(yuc(),vuc)),zuc(b,new Hsc('This is the first south component'),wuc),zuc(b,new Hsc('This is the east component'),suc),zuc(b,new Hsc('This is the west component'),xuc),zuc(b,new Hsc('This is the second north component'),vuc),zuc(b,new Hsc('This is the second south component'),wuc),a=new Hsc("This is a <code>ScrollPanel<\/code> contained at the center of a <code>DockPanel<\/code>.  By putting some fairly large contents in the middle and setting its size explicitly, it becomes a scrollable area within the page, but without requiring the use of an IFRAME.<br><br>Here's quite a bit more meaningless text that will serve primarily to make this thing scroll off the bottom of its visible area.  Otherwise, you might have to make it really, really small in order to see the nifty scroll bars!"),c=new epc(a),c.Q.style[A2c]=Lad,c.Q.style[y2c]=$bd,zuc(b,c,ruc),Auc(b,'cwDockPanel'),b))};_=Huc.prototype=quc.prototype=new Rnc;_.gC=function Iuc(){return Itb};_.tb=function Juc(a){Auc(this,a)};_.Lb=function Kuc(a){var b;b=ymc(this,a);if(b){a==this.a&&(this.a=null);Buc(this)}return b};_.cM={40:1,46:1,84:1,91:1,92:1,95:1,110:1,112:1};_.a=null;var ruc,suc,tuc,uuc,vuc,wuc,xuc;_=Muc.prototype=Luc.prototype=new Y;_.gC=function Nuc(){return Ftb};_=Puc.prototype=Ouc.prototype=new Y;_.gC=function Quc(){return Gtb};_.cM={88:1};_.a=null;_.c=null;_=Suc.prototype=Ruc.prototype=new Y;_.gC=function Tuc(){return Htb};_.cM={89:1};_.a=0;_.b=null;var Itb=cSc(p7c,'DockPanel'),Htb=cSc(p7c,'DockPanel$TmpRow'),_yb=bSc(Z7c,'DockPanel$TmpRow;',Htb),Ftb=cSc(p7c,'DockPanel$DockLayoutConstant'),Gtb=cSc(p7c,'DockPanel$LayoutData');v2c(tj)(29);