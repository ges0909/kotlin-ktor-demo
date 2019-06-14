<html>
    <body>
        <div>Logged in as: ${displayName}</div>
        <ul>
            <#list persons as person>
                <li>${person.name}, ${person.age}</li>
            </#list>
        </ul>
        <form method="post" action="/webapp/persons">
            <label for="name">Name:</label> </br>
            <input id="name" type="text" name="name" /> </br>
            <label for="age">Age:</label> </br>
            <input id="age" type="text" name="age" /> </br>
            <input type="submit" submit="Submit" />
        </form>
    </body>
</html>
