# MojangAPI-Wrapper
Light-Weight wrapper that allows you to make requests to Mojang's API. More features will be added as seen fit.
# Installation
Add the MojangRequest.jar and the dependencies to your build path.
# Dependencies
json-simple: https://github.com/fangyidong/json-simple
# Example
```Java
public static MojangRequest requests = new MojangRequest();
public static void main(String[] args) throws Exception{
    //Get UUID Method
    System.out.print(requests.getUUID("Specifies"));
    /*
    * Output:
    * Response Code - The getUUID request returned the response code of: 200
    * UUID of Specifies - 361dda429c1743fabb2149c60cf75d94
    */
    //NameHistory method
    System.out.print(requests.nameHistory("361dda429c1743fabb2149c60cf75d94"));
    /*
    * Output:
    * Response Code, JSON String that can then be parsed for data using json-simple.
    */
    //Get Original User method
    System.out.print(requests.getOriginalUser("Specifies", "1"));
    /*
    * Output:
    * Some information on the method.
    * JSON String that can be parsed, for the sake of explaining this method I provide a parsed example.
    * ID: ea1405e1bb7c41baba34f11a78fc4718
    * Name: Odoxxy
    * This means, that the original account owner of "Specifies(At timestamp of 2)" now has the name "Odoxxy"
    */
    //Get skin method
    System.out.print(requests.getSkin("361dda429c1743fabb2149c60cf75d94"));
    /*
    * Output:
    * Response code
    * Link to the inputted player's skin, in the case of this UUID: 
    * http://textures.minecraft.net/texture/28d93f7c3e3a87647db4224aa02a0e4ac854855953e6fe9855d1d20c7243a37b
    * (Note, a client can only request the same name once per minute due to the rate limit Mojang has set)
    */
    //Get blocked server hashes
    System.out.print(requests.getBlockedServerHashes());
    /*
    * Output:
    * Response code
    * An ArrayList that returns 40 character hashes that Mojang has blocked due to EULA violation
    */
```
