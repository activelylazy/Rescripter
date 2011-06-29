# What Is It?

*Rescripter* is an Eclipse plug-in that let's you define how to refactor 
Java code by writing JavaScript.

# Why Would I Want That?

If you've ever found yourself making large-scale changes that you can describe
easily but are laborious to do by hand - then *Rescripter* is for you.

# Where Do I Get It?

In Eclipse go to Help | Install New Software...
Create a new update site with the URL:
    https://raw.github.com/activelylazy/Rescripter/master/update-site/

# An Example Would Help

Say you want to replace a constructor call
    new MyNumber("123")
With a call to a static factory method, that is imported for brevity
    import static com.example.MyNumber.valueOf;
    ...
    valueOf("123");
Then you can do this by running the following .rs script:
```Javascript
var myNumber = Find.typeByName("MyNumber");
var cons = findMethodByName(myNumber, "MyNumber");
replaceConstructorCall(cons, "valueOf", true);
```
