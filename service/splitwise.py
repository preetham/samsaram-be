from splitwise import Splitwise, Expense, Category

from config.settings import app_config

_client = Splitwise(app_config.SPLITWISE_CONSUMER_KEY, app_config.SPLITWISE_CONSUMER_SECRET)

def login(domain) -> str:
    redirect_url = domain + app_config.REDIRECT_ENDPOINT
    url, state = _client.getOAuth2AuthorizeURL(redirect_url)
    return url

def authorize(code, domain):
    redirect_url = domain + app_config.REDIRECT_ENDPOINT
    token = _client.getOAuth2AccessToken(code, redirect_url)
    if not token:
        return None
    _client.setOAuth2AccessToken(token)
    user = _client.getCurrentUser()
    return dict({
        'id': user.getId(),
        'first_name': user.first_name,
        'last_name': user.last_name,
        'picture': user.getPicture().getSmall(),
    })

def groups():
    groups = _client.getGroups()
    response = list()
    for group in groups:
        g_json = dict({
            'id': group.getId(),
            'name': group.getName(),
            'type': group.getGroupType()
        })
        response.append(g_json)
    return response

def categories():
    categories = _client.getCategories()
    response = list()
    for category in categories:
        c_json = dict({
            'id': category.getId(),
            'name': category.getName()
        })
        response.append(c_json)
        for sub_cat in category.getSubcategories():
            sub_json = dict({
                'id': sub_cat.getId(),
                'name': sub_cat.getName()
            })
            response.append(sub_json)
    return response

def create_expense(raw_expenses):
    created_expenses = list()
    for e in raw_expenses:
        expense = Expense()
        expense.setGroupId(e['group_id'])
        expense.setDescription(e['description'])
        expense.setCurrencyCode(app_config.SPLITWISE_RUPEE_CODE)
        expense.setCost(e['cost'])
        expense.setSplitEqually(True)

        category = Category()
        category.setId(e['category_id'])

        expense.setCategory(category)
        expense.setDate(e['date'])

        created_expense, err = _client.createExpense(expense)
        if err:
            print(err.getErrors())
            continue
        created_expenses.append(dict({
            'id': created_expense.getId(),
            'description': created_expense.getDescription(),
        }))
    return created_expenses