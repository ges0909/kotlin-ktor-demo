<#import "common/bootstrap.ftl" as b>

<@b.page>
<#if persons?? && (persons?size > 0)>
<table class="table table-striped">
    <thead>
        <tr>
            <th>Name</th>
            <th>Age</th>
        </tr>
    </thead>
    <tbody>
        <#list persons as person>
            <tr>
                <td style="vertical-align:middle">${person.name}</td>
                <td style="vertical-align:middle">${person.age}</td>
                <td class="col-md-1" style="text-align:center;vertical-align:middle">
                    <form method="post" action="/persons">
                        <input type="hidden" name="date" value="${date?c}" />
                        <input type="hidden" name="code" value="${code}" />
                        <input type="hidden" name="id" value="${person.id}" />
                        <input type="hidden" name="action" value="delete" />
                        <input type="image" src="/static/delete.png" width="24" height="24" style="border:0" alt="Delete" />
                    </form>
                </td>
            </tr>
        </#list>
    </tbody>
</table>
</#if>

<div class="panel-body">
    <form method="post" action="/persons">
        <input type="hidden" name="date" value="${date?c}" />
        <input type="hidden" name="code" value="${code}" />
        <input type="hidden" name="action" value="add" />
        <label for="name">Name:</label> <br />
        <input type="text" name="name" /> <br />
        <label for="age">Age:</label> <br />
        <input type="text" name="age" /> <br /> <br />
        <input type="submit" submit="Submit" />
    </form>
</div>
</@b.page>
