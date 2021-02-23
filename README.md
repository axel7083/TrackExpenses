# TrackExpenses

You can select a period of time and an amount of money, then the app will give you a "Weekly allowance" defining how much you can spend per week to reach the amount inputed over the period.

The weekly allowance will vary depending on your expenses, if you spend more that what is recommanded in a week, you will reduce the allowance of the following weeks, and it is equivalent for spending less, you will increase the weeks left allowances.

See screenshots [Here](https://github.com/axel7083/TrackExpenses/tree/main/Docs) 

### TODO:

## Settings page
- [x] Create a setting activity
- [x] allow to edit total amount
- [x] allow to edit period of time (end date only?) => refractor system for weekly allowance
- [x] allow to edit currency sign
- [ ] Backup system (Save app version with it)
- [x] Choose what must be displayed in stat fragment (Choose type of graph, components etc..)

### Update system
- [ ] Use alert dialog to see what is new
- [ ] writte onUpgrade function properly to be able to upgrade in DB version
- [x] Display app info in settings

### General 
- [x] Create history_activity to display expense of specifics weeks
- [ ] Make an expense able to be "global" does not count in a week (such as rent)

### Design
- [x] Remove clickable layout expense_activity on label edittext
- [ ] Add a "global expense" switch in expense_activity (default: false)
- [x] Setting button in top right corner of main_activity
- [ ] Make categories in graph clickable => open activity to display all expenses for this category.
