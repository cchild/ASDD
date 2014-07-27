package V_RuleLearner;



//RULELEARNER MSDD


//        TokenMap t = new TokenMap();
//        t.fromFile();
//        
//        SensorList sMap = new SensorList();
        
        
//        Sensor test = new Sensor(t);
//        System.out.println("SENSOR TEST : " + test.tokens);
//        int n = t.TokenTypes.size();
        
        
        
        // LEVEL 1
        // LEVEL 1
        // LEVEL 1
        
//        for (int h = 0; h < n; h++) {
//            //test.tokens.set(h, t.TokenTypes.get(h).get(h));
//            int refMax = t.getRefMax(h);
//            
//            
//            for (int j = 1; j <= refMax; j++) {
//            
//            Sensor s = new Sensor(t);
//            Token a = new Token(j,h,t);
//            s.tokens.set(h, a);
//            sMap.addSensor(s);
//            
//            System.out.println("Pos : " + h + " Ref : " + j + " L1 Sensor : " + s.tokens);
//            }
//            //System.out.println(test.tokens.toString());
//        }
        
        // LEVEL 2
        // LEVEL 2
        // LEVEL 2
        
//        for (int h = 0; h < n-1; h++) {
//            //test.tokens.set(h, t.TokenTypes.get(h).get(h));
//            int refMax = t.getRefMax(h);
//            
//            
//            for (int j = 1; j < refMax; j++) {
//            
//            Sensor s = new Sensor(t);
//            Token a = new Token(j,h,t);
//            s.tokens.set(h, a);
//            
//            for (int k=0; k < t.getTokenList(h).size()-1; k++) {
//                //t.getTokenList(h).size()-1
//            Token b = new Token(j+1,k,t);
//            s.tokens.set(h+1, b);
//            sMap.addSensor(s);
//            
//            System.out.println("Pos : " + h + " Ref : " + j + " L2 Sensor : " + s.tokens);
//            }
//            }
//            //System.out.println(test.tokens.toString());
//        }
        
        
        //System.out.println("SENSOR_MAP HAS " + sMap.Sensors.size() + " ENTRIES.");
        //System.out.println(sMap.getSensor(3).tokens);
        //System.out.println();
        //System.out.println(t.TokenTypes.size());
        
        
        
        
        
//        
//        Sensor s1 = new Sensor("EWEEES",t);
//        Sensor s2 = new Sensor("W*E*E*",t);
//        
//        //System.out.println(s1.getString().toString() + s2.getString().toString());
//        SensorList sMap2 = new SensorList();
//        
//        //sMap2 = s2.expand(5);
//        //sMap2 = s2.expand(1);
//        //sMap2.addSensorMap(s2.expand(1));
//        
//        
//        for (int u = 1; u <= sMap2.Sensors.size(); u++){
//        System.out.println(sMap2.getSensor(u).getString());
//        }
//        
//        //sMap.getSensor(1);
//        //System.out.println("WILD" + s1.getToken(0).isWildcard());
//        
//        
//        //System.out.println(s1.getString() + " & " + s2.getString());
////        if (s1.equals(s2)) {
////            System.out.println(s1.getString() + " = " + s2.getString());
////        }
//        //System.out.println(s1.getString() + " = " + s2.getString());
//        
//        Sensor pre1 = new Sensor("EWEEES",t);
//        Sensor post1 = new Sensor("*WEE**",t);
//        Sensor pre2 = new Sensor("EWWEES",t);
//        Sensor post2 = new Sensor("*WEE*S",t);
//        
//        Rule r1 = new Rule(pre1,post1);
////        System.out.println(r1.toString());
//        Rule r2 = new Rule(pre2,post2);
//        
//        Rule r3 = new Rule(pre2,post2);
//        
//        Rule r4 = new Rule(pre1,post2);
//        
//        Rule r5 = new Rule(pre1,post1);
//        
//        RuleList rulelist = new RuleList();
//        
//        
//        rulelist.addRule(r1);
//        rulelist.addRule(r2);
//        rulelist.addRule(r3);
//        rulelist.addRule(r4);
//        rulelist.addRule(r5);
        
        
        //int res = rulelist.findRule(r1);
        //int res2 = rulelist.findRule_exact(r1);
//        System.out.println("R3 pre " + r3.precondition.toString());
//        System.out.println("R2 pre " + r2.precondition.toString());
        //System.out.println("Rulematch " + r3.sensorMatch(r3.precondition, r3.precondition));
        //System.out.println("RULE " + r1.ruleMatch_exact(r4));
        //System.out.println(pre1.getToken(2).toString().compareTo(pre1.getToken(2).toString()));
//        System.out.println("Hashcode " + r2.precondition.hashCode());
        //System.out.println("Rule r1 has been found " + res + " times");
        //System.out.println("Rule r1 has exactly been found " + res2 + " times");
        //System.out.println(pre1.getString().toString() + post1.getString().toString());
        //System.out.println(pre2.getString().toString() + post2.getString().toString());
//        Rule rule = new Rule (pre1,post1);
//        
////        System.out.println("Precondition : " + rule.precondition.getString());
////        System.out.println("Postcondition : " + rule.postcondition.getString());
////        System.out.println(rule.sensorMatch(s1,s2));
//        
//        System.out.println("RULE MATCH IS : " + rule.ruleMatch(pre2,post2));
//
//        
//        
//        System.out.println("TEST");
//        
//        String ss1 = "K*ELLO";
//        String ss2 = "*";
//        Token t1 = new Token("*",0,t);
//        Token t2 = new Token(0,0,t);
//        
//        Sensor sep = new Sensor("HEW*LE",t);
//        System.out.println("SEP : " + sep.getString());
//        
//        
//        System.out.println("SEP (3) WILDCARD ? " + sep.getToken(3).isWildcard());
        
//        SensorList slist = new SensorList();
//        
//        slist.fromFile(t);
//        slist.printList();
        
//        String str = "W*****";
//        String str2 = "E*****";
//        
//        Sensor foo = new Sensor(str,t);
//        Sensor foo2 = new Sensor(str2,t); 
//        
//        
//        Rule rule = new Rule(foo,foo2);
//        boolean res = slist.containsRule(rule);
//        //System.out.println(slist.firstIndexOfSensor(rule.precondition));
//        //System.out.println(slist.firstIndexOfSensor(rule.postcondition));
//        System.out.println("\nCONTAINS RULE " + rule + ": " + res);
//        
////        int res2 = slist.indexesOfRule(rule);
////        System.out.println("RULE PREC. INDEX : " + res2);
//        
//        ArrayList <Integer> a = slist.indexesOfSensor(rule.precondition);
//        
//        System.out.println("INDEX OF PREC. : " + a);
//        //System.out.println(a.size());
//        
//        ArrayList <Integer> b = slist.indexesOfSensor(rule.postcondition);
//        
//        System.out.println("INDEX OF POSTC. : " + b);
//        
//        
//        
//        //System.out.println("/// : " + b.contains((a.get(0)+20)));
//        
//        
//        ArrayList result = slist.indexOfRule(rule);
//        
//        System.out.println("INDEXES OF RULE : " + result);
//        
//        double ruleOcc = result.size();
//        double rulePrecOcc = a.size();
//        double prob = ruleOcc / rulePrecOcc;
//        
//        //double prob = 1d / 4d;
//        System.out.println("PROBABILITY OF RULE : " + prob);
        
        //System.out.println("INDEX " + slist.firstIndexOfSensor(foo));
        //int occ = slist.findSensor(slist.getSensor(2));
        
//      
//        int occ = slist.findSensor(foo);
//        System.out.println(foo + " found " + occ + " times.");
//        
//        int occ2 = slist.findSensor(foo2);
//        
//        System.out.println(foo2 + " found " + occ2 + " times.");
//        
//        int occ3 = slist.findSensor(foo2);
//        
//        System.out.println(foo2 + " found " + occ3 + " times.");
        
//        int tip1 = slist.firstIndexOfSensor(foo);
//        System.out.println("INDEX " + tip1);



       // [W, E, *, *, *, N][E, *, *, *, *, *]
       
//       Sensor a = new Sensor("WE***N",t);
//       Sensor b = new Sensor("E*****",t);
//       Rule rit = new Rule(a,b);
//       System.out.println(slist.indexOfRule(rit));
        // GENERATING LEVEL 3 RULES
        // GENERATING LEVEL 3 RULES
        // GENERATING LEVEL 3 RULES
//        
//        if (level3) {
//        RuleList level3_pull = new RuleList (rulelist);
//        
//        level3_pull = level3_pull.removeUnexpandable();
//        
//        for (int i=0; i< level3_pull.size(); i++) {
//            SensorList pre_children = level3_pull.getRule(i).precondition.expand(level3_pull.getRule(i).precondition.isExpandable());
//            SensorList post_children = level3_pull.getRule(i).postcondition.expand(level3_pull.getRule(i).postcondition.isExpandable());
//            //System.out.println(level2_pull.getRule(i).postcondition);
//            
//            //pre_children.printList();
//            //post_children.printList();
//            for (int j=0; j< pre_children.size(); j++) {
//                for(int h=0; h<post_children.size();h++) {
//                    Rule rule = new Rule(pre_children.getSensor(j+1),post_children.getSensor(h+1));
//                    rule.occurrencies = slist.indexOfRule(rule).size();
//                    if ((pruning && rule.occurrencies > 0) || (!pruning)) {
//                            rulelist.addRule(rule);
//                            ruleOcc = rule.occurrencies;
//                            rulePrecOcc = slist.indexesOfSensor(rule.precondition).size();
//                            
//                            double proba = ruleOcc / rulePrecOcc;
//                            proba = Math.round(proba * 100);
//                            proba = proba/100;
//                            
//                            
//                            prob.add(proba);
//                            totalnodes++;
//                    }
//                }
//            }
//        }
//        
//        rulelist.printList("LEVEL 3",prob);
//        }
//        Sensor a = new Sensor("AWE*ES",t);
//        System.out.println(a.isExpandable());
        //System.out.println();
        //rulelist.printList("LEVEL 2");