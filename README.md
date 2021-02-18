# TrackExpenses

You can select a period of time and an amount of money, then the app will give you a "Weekly allowance" defining how much you can spend per week to reach the amount inputed over the period.

The weekly allowance will vary depending on your expenses, if you spend more that what is recommanded in a week, you will reduce the allowance of the following weeks, and it is equivalent for spending less, you will increase the weeks left allowances.

### TODO:

## Settings page
- [ ] Create a setting activity
- [ ] allow to edit total amount
- [ ] allow to edit period of time (end date only?) => refractor system for weekly allowance
- [ ] allow to edit currency sign
- [ ] allow to edit current name (see general  "ask name...")
- [ ] Backup system (Save app version with it)
- [ ] Choose what must be displayed in stat fragment (Choose type of graph, components etc..)

### Update system
- [ ] Use alert dialog to see what is new
- [ ] writte onUpgrade function properly to be able to upgrade in DB version
- [ ] Display app info in settings

### General 
- [ ] Create week_expense_activity to display expense of specifics weeks
- [ ] Make an expense able to be "global" does not count in a week (such as rent)
- [ ] Ask name in intro and save it into settings

### Design
- [ ] Remove clickable layout expense_activity on label edittext
- [ ] Add a "global expense" switch in expense_activity (default: false)
- [ ] Overview section in stats clickable ? => more info
- [ ] Setting button in top right corner of main_activity
- [ ] Make categories in graph clickable => open activity to display all expenses for this category.
