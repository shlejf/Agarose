
(
s = Server.scsynth.local.boot;
 )


// stack
(
SynthDef.new(\constant,
 {|p,v=0|
  ReplaceOut.ar(p,K2A.ar(v));
  }).add;

SynthDef.new(\l,
 {|p|
 Out.ar(0,In.ar(p,1));
  }).add;

SynthDef.new(\r,
 {|p|
 Out.ar(1,In.ar(p,1));
  }).add;

SynthDef.new(\a,
 {|p| ReplaceOut.ar(p,In.ar(p,1) + In.ar(p+1,1));}).add;

SynthDef.new(\s,
 {|p| ReplaceOut.ar(p,In.ar(p,1) - In.ar(p+1,1));}).add;

SynthDef.new(\m,
 {|p| ReplaceOut.ar(p,In.ar(p,1) * In.ar(p+1,1));}).add;

SynthDef.new(\d,
 {|p| ReplaceOut.ar(p,In.ar(p,1) / In.ar(p+1,1));}).add;

SynthDef.new(\o,
 {|p| ReplaceOut.ar(p,In.ar(p,1) % In.ar(p+1,1));}).add;

SynthDef.new(\sl,
 {|p| ReplaceOut.ar(p,In.ar(p,1) << In.ar(p+1,1));}).add;

SynthDef.new(\sr,
 {|p| ReplaceOut.ar(p,In.ar(p,1) >> In.ar(p+1,1));}).add;

SynthDef.new(\sin,
 {|p|
 ReplaceOut.ar(p,SinOsc.ar(In.ar(p,1),0,1));
  }).add;

SynthDef.new(\saw,
 {|p|
 ReplaceOut.ar(p,Saw.ar(In.ar(p,1),1,0));
  }).add;

SynthDef.new(\verb,
 {|p|
 ReplaceOut.ar(p,FreeVerb.ar(In.ar(p,1),In.ar(p+1,1),In.ar(p+2,1),In.ar(p+3,1),1,0));
  }).add;

SynthDef.new(\moog,
 {|p|
 ReplaceOut.ar(p,MoogFF.ar(In.ar(p,1),In.ar(p+1,1),In.ar(p+2,1),0,1,0));
  }).add;


SynthDef.new(\pi,
 {|p|
 ReplaceOut.ar(p,PitchShift.ar(In.ar(p,1),0.1,In.ar(p+1,1),0,0,1,0));
  }).add;


~tape = Array.newClear(1024);
~point = 4;
~end = 0;

~go = {|string|
 var tape = string.tr($\n,$ ).split($ ).select({|char,i| char != ""});
 var tapesize = tape.size;
 
 ~point = 4;
 
 tape.do({|token,i|
   var op = ~tape.at(i);
   var name = token.asSymbol;
   var oldname = if ((op == nil), {nil}, {op.defName}); 
   var identifier = name.isIdentifier;

   ~point = ~point + if(identifier,
            {switch (name)
	    
             {\l}    {0}
             {\r}    {0}	     
	     {\a}    {(-1)}
	     {\s}    {(-1)}
	     {\m}    {(-1)}	     
	     {\d}    {(-1)}	
	     {\o}    {(-1)}
	     {\sl}   {(-1)}
	     {\sr}   {(-1)}     	     
             {\sin}  {0}
             {\saw}  {0}	     
             {\verb} {(-3)}
	     {\moog} {(-2)}
	     {\pi}   {(-1)}	     
	     },
	    {1});
 
   if(identifier,
    {if(oldname == \constant,
     {~tape.put(i,op.replace(name,[\p,~point],true))},
     {if(oldname == nil,
      {~tape.put(i,Synth.new(name,[\p,~point],addAction:\addToTail))},
      {if(oldname == name,
       {op.set(\p,~point)},
       {~tape.put(i,op.replace(name,[\p,~point],true))})})})
    },
    {if(oldname == \constant,
     {op.set(\p,~point,\v,name.asFloat)},
     {if(oldname == nil,
       {~tape.put(i,Synth.new(\constant,[\p,~point,\v,name.asFloat],addAction:\addToTail))},
       {~tape.put(i,op.replace(\constant,[\p,~point,\v,token.asFloat],true))})})}
   )});

 if(tapesize < ~end,
  {for(tapesize,~end,
   {|i|
    ~tape.at(i).free;
    ~tape.put(i,nil);
   })},
  {nil});
  
 ~end = tapesize;
 
 ~tape;
};

)


// Examples

(~go.value("

40 sin 10 a
20 saw m
40 sin 1 a 10000 m 3.5 moog
20 sin a 3 saw 3 m pi
9003 sin a .25 m
.1 sin 1 a 1000 m 3 moog r l
a .7 .6 .5 verb .5 m
l .5 m .1 sin 1 a pi r 

30 saw
10 saw m
7 sin 2 a 2000 m 3 moog
650 sin .2 m
3000 sin a 3 saw .1 m pi
.1 sin 200 m .4 sin 5 m o 1 a sin 1 a 9000 m 3 moog r l
a .5 1 .9 verb .5 m
r 1 m 1 saw 5 sin 2 a 2 m a pi l .2 m r

"))


(~go.value("

.1 sin 30 m 3 sin 22 m o 51 m sin 1 .9 .0 verb .1 m 1 .99 .5 verb 5 sin m 1 .99 .5 verb l .5 m r
.1 sin 30 m 3 sin 22 m o 50 m sin 1 .9 .0 verb .1 m 1 .99 .5 verb 5 sin m 1 .99 .5 verb r .5 m l


"))
