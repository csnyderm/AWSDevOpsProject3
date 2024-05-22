import { Button, ModalToggleButton, ModalRef, Table, Modal, ModalHeading, ModalFooter, TextInput, Form, Label, Icon, CardGroup, Card, Dropdown } from "@trussworks/react-uswds";
import { ReactElement, useEffect, useRef, useState } from "react";
import { useAddPlannerMutation, useGetPlannerByEmailQuery, useUpdatePlannerMutation } from "../app/api/plannerApi";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../app/features/userSlice";
import deleteIcon from "../assets/delete_icon.png"
import { useTranslation } from "react-i18next";
import { HighchartsReact } from "highcharts-react-official";
import Highcharts from "highcharts";



export default function PlannerPage() {

    interface EditingObject {
        [id: number]: Array<boolean>;
    }


    interface Planner {
        email: string;
        categories: Array<any>;
        expenses: Array<ExpenseHTML>;
        monthlyIncome: number;

    }
    
    interface ExpenseHTML {
        id: number;
        category: string;
        expenseName: string;
        actualExpense: number;
        desiredExpense: number;
        dueDate: string;
    }

    const [chartData, setChartData] = useState<Array<any>>([]);

    const options = {
        credits: {
          enabled: false
        },
        chart: {
          type: 'pie',
         // backgroundColor: '#e0f7f6'
        },
        title: {
          text: '',
        },
        series: [
          {
            name: 'Data',
            data: chartData
                //[{ name: 'Federal Tax ' + fedLabel, y: Number(Math.abs(fedTax).toFixed(2)) }]
            ,
            colors: ["#33BBC5",
                "#85E6C5",
                "#C8FFE0",
            "#116530","#21B6A8", "#A3EBB1", "#18A558"]
          },
        ],
      };

    const userEmail: string = useSelector(selectUserEmail);
    //const userEmail : string = "fpolicastro@gmail.com";
    const { data, error, isLoading, isError, isFetching, refetch } = useGetPlannerByEmailQuery(userEmail);
    const [addPlanner] = useAddPlannerMutation();
    const [updatePlanner] = useUpdatePlannerMutation();
    const [readyForUpdates, setReadyForUpdates] = useState(false);
    //const [editing, setEditing] = useState<EditingObject>({1 : [false,false,false,false], 2 :[false,false,false,false], 3: [false,false,false,false], 4:[false,false,false,false], 5:[false,false,false,false], 6:[false,false,false,false]})
    //let editing : EditingObject = {1 : [false,false,false,false], 2 :[false,false,false,false], 3: [false,false,false,false], 4:[false,false,false,false], 5:[false,false,false,false], 6:[false,false,false,false]}
    const editing = useRef<EditingObject>({ 1: [false, false, false, false], 2: [false, false, false, false], 3: [false, false, false, false], 4: [false, false, false, false], 5: [false, false, false, false], 6: [false, false, false, false] })
    const [editingIncome, setEditingIncome] = useState(false);
    const addCategoryModalRef = useRef<ModalRef>(null);
    const addItemModalRef = useRef<ModalRef>(null);
    const [currentCategory, setCurrentCategory] = useState("");
    const idCounter = useRef(0);
    const [tableBody, setTableBody] = useState([<></>])
    const [testMonthlyIncome, setTestMonthlyIncome] = useState(0);
    const [testCategories, setTestCategories] = useState<Array<string>>([]);
    //const [testCategories, setTestCategories] = useState(["Personal", "Rent", "Groceries", "Subscriptions"]);
    const [testExpenses, setTestExpenses] = useState<Array<ExpenseHTML>>([]);
    const {t, i18n} = useTranslation();
    const [calculationValues, setCalculationValues] = useState<Array<any>>([null,null,null]);

    /*const [testExpenses, setTestExpenses] = useState<Array<ExpenseHTML>>([{id: 1, category : "Personal",
                           expenseName : "Subscription",
                           actualExpense : 450,
                           desiredExpense : 400,
                           dueDate : "09/07/23"},
                           {id: 2 , category : "Rent",
                           expenseName : "Apartment",
                           actualExpense : 200,
                           desiredExpense : 250,
                           dueDate : "09/15/23"},
                           {id : 3 , category : "Groceries",
                           expenseName : "Milk",
                           actualExpense : 112,
                           desiredExpense : 112,
                           dueDate : "09/10/23"},
                           {id : 4 , category : "Personal",
                           expenseName : "Cell Phone",
                           actualExpense : 85,
                           desiredExpense : 85,
                           dueDate : "Monthly"},
                           {id : 5, category : "Rent",
                           expenseName : "Utilities",
                           actualExpense : 75,
                           desiredExpense : 85,
                           dueDate : "09/12/23"},
                           {id : 6, category : "Groceries",
                           expenseName : "Meat",
                           actualExpense : 250,
                           desiredExpense : 250,
                           dueDate : "09/25/23"}]);*/

    

    useEffect(() => {
        if (!isFetching) {
            if (isError) {
                let theError: any = error;
                if (theError.data.status == 500) {
                    addPlanner({
                        email: userEmail,
                        categories: ["Housing", "Subscriptions"],
                        expenses: [],
                        monthlyIncome: 1000
                    })
                }
            }
            if (data != undefined) {
                let newExpenses: Array<ExpenseHTML> = []
                let newEditing: EditingObject = {};
                data.expenses.forEach((expense) => {
                    let newExpense: ExpenseHTML = { ...expense, id: idCounter.current }
                    newExpenses.push(newExpense);
                    newEditing[newExpense.id] = [false, false, false, false]
                    idCounter.current++;
                })
                editing.current = newEditing;
                setTestExpenses(newExpenses);
                setTestCategories(data.categories);
                setTestMonthlyIncome(data.monthlyIncome);
                setReadyForUpdates(true);
            }
        }
    }, [isLoading])

    useEffect(() => {
        updateTable();
        if (readyForUpdates) {
            const modifiedExpenses: Array<any> = [];
            testExpenses.forEach((expense) => {
                modifiedExpenses.push({
                    category: expense.category,
                    expenseName: expense.expenseName,
                    actualExpense: expense.actualExpense,
                    desiredExpense: expense.desiredExpense,
                    dueDate: expense.dueDate
                })
            });
            updatePlanner({
                email: userEmail,
                expenses: modifiedExpenses,
                categories: testCategories,
                monthlyIncome: testMonthlyIncome
            })
        }


    }, [testCategories, testExpenses, testMonthlyIncome, i18n.language])

    useEffect(() => {
        if (currentCategory != "") {
            addItemModalRef.current?.toggleModal();
        }
    }, [currentCategory])


    function updateTable() {
        setTableBody(createTableBody())
    }

    function createTableBody() {
        let totalDesired: number = 0;
        let totalActual: number = 0;
        let output: Array<ReactElement> = []
        let backgroundColorAlternator = true;
        let categoryTotals : Map<string, number> = new Map<string,number>();
        testCategories.forEach((category) => {
            /*let backgroundClass = ""
            if(backgroundColorAlternator){
                backgroundClass = "plannerTableRowA"
            }
            else{
                backgroundClass = "plannerTableRowB"
            }*/
            output.push(
                <tr key={category} className={"plannerTableRowA" + " plannerTbodyTr"}>
                    <td key={category + " 0"} className="plannerTdTh" style={{fontWeight:"bold"}}>
                        {category}
                        {/*<Button key={category + " 0 button"} type="button" onClick={deleteCategory} value={category} secondary>Del Category</Button>*/}
                    </td>
                    <td key={category + " 1"} className="minw-10 plannerTdTh" colSpan={1}>
                        {/*<Button key={category + " 1 button"} type="button" onClick={openAddItemModal} value={category} data-open-modal aria-controls="addItemModal">Add Item</Button>*/}
                        <Icon.AddCircle size={4} className="text-mint plannerHoverPointer" key={category + " 1 iconI"} data-open-modal aria-controls="addItemModal" onClick={() => openAddItemModal(category)}></Icon.AddCircle>
                    </td>
                    <td key={category + " 2"} className="plannerTdTh" colSpan={3}></td>
                    <td key={category + " 3"} className="plannerTdTh">
                        <img src={deleteIcon} className="text-middle plannerHoverPointer" style={{ width: "2em" }} key={category + " 0 imgI"} onClick={() => deleteCategory(category)}></img>
                    </td>
                </tr>
            )
            testExpenses.forEach((expense) => {
                if (category == expense.category) {
                    let adjustedDesired = Number(expense.desiredExpense);
                    let adjustedActual = Number(expense.actualExpense);
                    switch(expense.dueDate){
                        case "Biweekly": {
                            adjustedActual *= 2;
                            adjustedDesired *= 2;
                            break;
                        }
                        case "Weekly": {
                            adjustedActual *= 4;
                            adjustedDesired *= 4;
                            break;
                        }
                    }
                    if(categoryTotals.has(category)){
                        let currentTotal = categoryTotals.get(category)
                        if(currentTotal != undefined){
                            categoryTotals.set(category, currentTotal + adjustedActual);
                        }
                    }
                    else{
                        categoryTotals.set(category, adjustedActual);
                    }

                    totalDesired += adjustedDesired;
                    totalActual += adjustedActual;
                    output.push(

                        <tr style={{ height: "4em" }} key={expense.id} className={"plannerTableRowB" + " plannerTbodyTr"}>
                            <td key={expense.id.toString() + " 0"} onClick={() => editItem([expense.id, 0])} className="plannerHoverPointer text-indent-4 plannerTdTh">
                                {editing.current[expense.id][0] ?
                                    <TextInput autoFocus inputSize="small" key={expense.id.toString() + " 0 textI"} onBlur={(e) => { saveEdits(e, expense.id, 0) }} onKeyDown={(e) => { saveEdits(e, expense.id, 0) }} type="text" style={{ width: (expense.expenseName.length + 4).toString() + "ch" }}
                                        defaultValue={expense.expenseName} id={"budget" + expense.id.toString() + "," + "0"} name={expense.id.toString() + "," + "0"}></TextInput>
                                    :
                                    expense.expenseName

                                }

                            </td>
                            <td key={expense.id.toString() + " 1"} className="plannerTdTh">
                                {/*<Button key={expense.id.toString() + " 1 button"} type="button" value={expense.id} onClick={deleteValue} secondary>Del</Button>*/}
                            </td>
                            <td key={expense.id.toString() + " 2"} className="plannerHoverPointer plannerTdTh" onClick={() => editItem([expense.id, 1])}>
                                $ {editing.current[expense.id][1] ?
                                    <TextInput className="display-inline-block" autoFocus style={{ width: "6em" }} inputSize="small" key={expense.id.toString() + " 2 textI"} onBlur={(e) => { saveEdits(e, expense.id, 1) }} onKeyDown={(e) => { saveEdits(e, expense.id, 1) }} type="number" defaultValue={expense.desiredExpense} id={"budget" + expense.id.toString() + "," + "1"} name={expense.id.toString() + "," + "1"}></TextInput>
                                    :
                                    expense.desiredExpense
                                }
                            </td>
                            <td key={expense.id.toString() + " 3"} className="plannerHoverPointer plannerTdTh" onClick={() => editItem([expense.id, 2])}>
                                $ {editing.current[expense.id][2] ?
                                    <TextInput className="display-inline-block" autoFocus style={{ width: "6em" }} inputSize="small" key={expense.id.toString() + " 3 textI"} onBlur={(e) => { saveEdits(e, expense.id, 2) }} onKeyDown={(e) => { saveEdits(e, expense.id, 2) }} type="number" defaultValue={expense.actualExpense} id={"budget" + expense.id.toString() + "," + "2"} name={expense.id.toString() + "," + "2"}></TextInput>
                                    :
                                    expense.actualExpense

                                }
                            </td>
                            <td key={expense.id.toString() + " 4"} className="plannerHoverPointer plannerTdTh" onClick={() => editItem([expense.id, 3])}>
                                {/*<TextInput style={{width:"6em"}} inputSize="small" key={expense.id.toString() + " 4 textI"} onKeyDown={(e) => {saveEdits(e,expense.id,3)}} type="text" defaultValue={expense.dueDate} id={"budget"+expense.id.toString() +"," +"3"} name={expense.id.toString() +","+"3"}></TextInput>*/}
                                {editing.current[expense.id][3] ?

                                    <Dropdown autoFocus onBlur={(e) => { saveEdits(e, expense.id, 3) }} onKeyDown={(e) => { saveEdits(e, expense.id, 3) }} defaultValue={expense.dueDate} id={"budget" + expense.id.toString() + "," + "3"} name={expense.id.toString() + "," + "3"}>
                                        <option value="Monthly">Monthly</option>
                                        <option value="Biweekly">Biweekly</option>
                                        <option value="Weekly">Weekly</option>
                                    </Dropdown>
                                    :
                                    expense.dueDate
                                }
                            </td>
                            <td key={expense.id.toString() + " 5"} className="plannerTdTh">
                                <img className="text-middle plannerHoverPointer" style={{ width: "2em" }} key={expense.id.toString() + " 1 delIcon"} onClick={() => { deleteValue(expense.id) }} src={deleteIcon}></img>
                            </td>
                        </tr>

                    )
                }
            })
            backgroundColorAlternator = !backgroundColorAlternator;
        })
        output.push(<tr className="plannerTbodyTr" key={"totalLine"}>
            <td key={"totalLine -1"} className="plannerTdTh"><ModalToggleButton style={{ borderRadius: "10px" }} className="bg-mint" modalRef={addCategoryModalRef} opener>{t("planner.addCategory")}</ModalToggleButton></td>
            <td key={"totalLine 0"} className="plannerTdTh"></td>
            <td key={"totalLine 1"} className="plannerTdTh">$ <strong className="text-mint">{totalDesired}</strong></td>
            <td key={"totalLine 2"} className="plannerTdTh">$ <strong className="text-mint">{totalActual}</strong></td>
            <td key={"totalLine 3"} className="plannerTdTh"></td>
        </tr>)
        let discrepancy = totalDesired - totalActual;
        let desiredLeftover = testMonthlyIncome - totalDesired;
        let actualLeftover = testMonthlyIncome - totalActual;
        setCalculationValues([discrepancy,desiredLeftover,actualLeftover])
        
        let newPieChartData : Array<any> = []
        categoryTotals.forEach((value, key) =>{
            newPieChartData.push({
                name: key,
                y: value,
            })
        })
        setChartData(newPieChartData);
        return output;
    }

    function editItem(infoArray: Array<number>) {
        let itemId = infoArray[0];
        let desiredActual = infoArray[1]
        /*setEditing((oldEditing) =>{
            oldEditing[itemId][desiredActual] = true
            return oldEditing
        })*/
        editing.current[itemId][desiredActual] = true
        updateTable();
    }

    function openAddItemModal(event: any) {
        //event.preventDefault();
        //let newValue : string = event.target.value;
        let newValue: string = event;
        var currentCategoryState
        setCurrentCategory((_current) => {
            currentCategoryState = _current;
            return _current;
        })
        if (newValue == currentCategoryState) {
            addItemModalRef.current?.toggleModal();
        }
        else {
            setCurrentCategory(newValue);
        }
    }

    function submitNewCategory(event: any) {
        event.preventDefault()
        const data = new FormData(event.target);
        const newCategory = data.get('newCategoryInput')?.toString();
        event.target.reset()
        if (newCategory != null) {
            setTestCategories((oldState) => {
                return [...oldState, newCategory];
            })
        }
        addCategoryModalRef.current?.toggleModal();
    }

    function submitNewItem(event: any) {
        event.preventDefault();
        const data = new FormData(event.target);
        const newExpense = {
            id: idCounter.current,
            category: currentCategory,
            expenseName: data.get("expenseNameInput")?.toString() + "",
            actualExpense: Number(data.get('actualExpenseInput')?.toString()),
            desiredExpense: Number(data.get('desiredExpenseInput')?.toString()),
            dueDate: data.get('dueDateInput')?.toString() + ""
        }
        idCounter.current++;

        /*setEditing((oldState) =>{
            let newState = oldState;
            newState[newExpense.id] = [false,false,false,false];
            return newState
        })*/
        editing.current[newExpense.id] = [false, false, false, false]


        setTestExpenses((oldState) => {
            return [...oldState, newExpense]
        })

        event.target.reset();

        addItemModalRef.current?.toggleModal();
    }

    function deleteValue(event: any) {
        //const deleteId = event.target.value;
        const deleteId = event;
        let testExpensesList: Array<ExpenseHTML> = testExpenses;
        let newExpensesList: Array<ExpenseHTML> = []
        /*
        setEditing(_oldState =>{
            let newState : EditingObject = _oldState;
            delete newState[deleteId];
            return newState;
        })*/
        delete editing.current[deleteId]
        testExpensesList.forEach((expense) => {
            if (expense.id != deleteId) {
                newExpensesList.push(expense);
            }
        })
        setTestExpenses(newExpensesList);
    }

    function deleteCategory(event: any) {
        //const categoryToDelete : string = event.target.value
        const categoryToDelete = event;
        let oldExpenses = testExpenses;
        let oldCategories = testCategories;
        let newExpenses: Array<ExpenseHTML> = [];
        let newCategories: Array<string> = [];

        oldExpenses.forEach(expense => {
            if (expense.category != categoryToDelete) {
                newExpenses.push(expense);
            }
        })
        oldCategories.forEach(category => {
            if (category != categoryToDelete) {
                newCategories.push(category);
            }
        })

        setTestCategories(newCategories);
        setTestExpenses(newExpenses);
    }

    function saveEdits(event: any, expenseId: number, index: number) {
        if (event.key == "Enter" || event.type == "blur") {
            const newValue = event.target.value;
            editing.current[expenseId][index] = false;
            setTestExpenses((oldExpenses) => {
                const newExpenses: Array<ExpenseHTML> = [...oldExpenses];
                newExpenses.forEach(expense => {
                    if (expense.id == expenseId) {
                        switch (index) {
                            case 0: {
                                expense.expenseName = newValue;
                                break;
                            }
                            case 1: {
                                expense.desiredExpense = newValue;
                                break;
                            }
                            case 2: {
                                expense.actualExpense = newValue;
                                break;
                            }
                            case 3: {
                                expense.dueDate = newValue;
                                break;
                            }
                        }

                    }
                })
                return newExpenses;
            })
            updateTable();
        }
    }

    function saveMonthlyIncome(event: any) {
        if (event.key == "Enter" || event.type == 'blur') {
            setTestMonthlyIncome(event.target.value);
            setEditingIncome(false);
        }
    }

    return (
        <>
            <div className="text-center margin-left-auto margin-right-auto">
                <h1>Monthly Budget Planner</h1>
                {/*<div className="text-left margin-4 margin-left-15">*/}
                <CardGroup className="display-inline-block margin-bottom-10">
                    <Card className="plannerCard monthlyIncome">
                        <span className="padding-0">
                            <h2 className="display-inline">{t("planner.monthlyIncome")} </h2>
                            {editingIncome ?
                                <TextInput autoFocus className="display-inline" style={{ width: "10em" }} id="income" name="income" type="number" defaultValue={testMonthlyIncome} onBlur={(e) => saveMonthlyIncome(e)} onKeyDown={(e) => saveMonthlyIncome(e)}></TextInput>
                                :
                                <h3 className="text-mint text-bold display-inline plannerHoverPointer" onClick={() => { setEditingIncome(true) }}>$ {testMonthlyIncome}</h3>
                            }
                        </span>
                        
                    </Card>
                    {/*</div>*/}
                    {/*<div className="bg-white display-inline-block padding-x-5 padding-y-1 radius-lg">*/}
                    <Card className="plannerCard">
                        <Table bordered className="margin-x-5 padding-y-1">
                            <thead>
                                <tr className="plannerTbodyTr">
                                    <th className="plannerTdTh" scope="colgroup" colSpan={2}><h3>{t("planner.expenses")}</h3></th>
                                    <th className="plannerTdTh" style={{ minWidth: "9em" }} scope="col"><h3>{t("planner.desiredExpense")}</h3></th>
                                    <th className="plannerTdTh" style={{ minWidth: "9em" }} scope="col"><h3>{t("planner.actualExpense")}</h3></th>
                                    <th className="plannerTdTh" style={{ minWidth: "9em" }} scope="col"><h3>{t("planner.dueDate")}</h3></th>
                                    <th className="plannerTdTh" style={{ minWidth: "4em" }} scope="col"></th>
                                </tr>

                            </thead>
                            <tbody className="plannerTbodyTr">
                                {tableBody}
                            </tbody>
                        </Table>
                        <Modal ref={addCategoryModalRef} id="addCategoryModal">
                            <ModalHeading style={{ fontFamily: "Source Sans Pro Web, Helvetica Neue, Helvetica, Roboto, Arial, sans-serif" }}>
                                {t("planner.addCategoryPrompt")}:
                            </ModalHeading>
                            <ModalFooter>
                                <Form onSubmit={submitNewCategory}>
                                    <TextInput type="text" name="newCategoryInput" id="newCategoryInput" />
                                    <Button type="submit" style={{ borderRadius: "10px" }} className="bg-mint">{t("planner.create")}</Button>

                                </Form>
                            </ModalFooter>
                        </Modal>
                        <Modal ref={addItemModalRef} id="addItemModal">
                            <ModalHeading style={{ fontFamily: "Source Sans Pro Web, Helvetica Neue, Helvetica, Roboto, Arial, sans-serif" }}>
                                {t("planner.addItemPrompt")}
                            </ModalHeading>
                            <ModalFooter>
                                <Form onSubmit={submitNewItem}>
                                    <Label htmlFor="expenseNameInput">{t("planner.expenseName")}</Label>
                                    <TextInput type="text" id="expenseNameInput" name="expenseNameInput"></TextInput>

                                    <Label htmlFor="desiredExpenseInput">{t("planner.desiredSpending")}</Label>
                                    <TextInput type="number" id="desiredExpenseInput" name="desiredExpenseInput"></TextInput>

                                    <Label htmlFor="actualExpenseInput">{t("planner.actualSpending")}</Label>
                                    <TextInput type="number" id="actualExpenseInput" name="actualExpenseInput"></TextInput>

                                    <Label htmlFor="dueDateInput">{t("planner.dueDate")}</Label>
                                    {/*<TextInput type="text" id="dueDateInput" name="dueDateInput"></TextInput>*/}
                                    <Dropdown id="dueDateInput" name="dueDateInput" defaultValue={"Monthly"}>
                                        <option value="Monthly">Monthly</option>
                                        <option value="Biweekly">Biweekly</option>
                                        <option value="Weekly">Weekly</option>
                                    </Dropdown>
                                    <Button type="submit" style={{ borderRadius: "10px" }} className="bg-mint">{t("create")}</Button>
                                </Form>

                            </ModalFooter>

                        </Modal>
                    </Card>
                    <Card className="plannerCard">
                        <ul className="margin-top-2" style={{listStyleType: "none"}}>
                            <li><strong>{t("planner.differenceDesired")}:</strong> $ <span className="text-mint">{calculationValues[0]}</span></li>
                            <li><strong>{t("planner.desiredLeftover")}:</strong> $ <span className="text-mint">{calculationValues[1]}</span> </li>
                            <li><strong>{t("planner.actualLeftover")}:</strong> $ <span className="text-mint">{calculationValues[2]}</span> </li>
                        </ul>
                        <div className="margin-4">
                            <HighchartsReact highcharts={Highcharts} options={options}/>
                        </div>
                    </Card>
                </CardGroup>
                {/*</div>*/}
            </div>
        </>
    )
}