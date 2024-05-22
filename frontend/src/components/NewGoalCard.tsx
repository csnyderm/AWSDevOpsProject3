import { Icon, Button, Card, CardBody, CardFooter, CardHeader, Fieldset, Label, TextInput, Form } from '@trussworks/react-uswds'
import { useState } from 'react'
import { useTranslation } from 'react-i18next';
import { useAddGoalMutation } from "../app/api/goalsApi";
import { selectUserEmail } from "../app/features/userSlice";
import { useSelector } from 'react-redux';
import ReactCardFlip from "react-card-flip";
import "../styles/goalCard.css"

export default function NewGoalCard() {

  const [flip, setFlip] = useState(false);

  //for I18n
  const {t} = useTranslation();



  const userEmail = useSelector(selectUserEmail).toString();

  //State for new Goal
  const [newGoal, setNewGoal] = useState(true)
  //Functiopn to update state
  function newGoalFunction(){
    setGoalAmount("")
    setSavedAmount("")
    setGoalName("")
    setFlip(!flip)
  
  }
  
  const [addGoalMutation, mutationResult] = useAddGoalMutation();

  //handle form submit
  async function newFormSubmit(event : any){
    
    event.preventDefault();
    const data = new FormData(event.target);

    if(data){
      const newGoalVariables = {
        email: String(userEmail),
        name: data.get('title')?.toString() || '',
        goalAmount: Number(data.get('goalAmount')) || 0,
        amountSaved: Number(data.get('amountSaved')) || 0,
      };
      try {
        await addGoalMutation(newGoalVariables);
        newGoalFunction()
       
      } catch(error){

      };
    }
  }

  //state to 
  const [goalAmount, setGoalAmount] = useState("");
  const handleInputChange = (e : any) => {
    setGoalAmount(e.target.value);
  }

  const [savedAmount, setSavedAmount] = useState("");
  const handleAmountChange = (e : any) => {
    setSavedAmount(e.target.value);
  }

  const [goalName, setGoalName] = useState("");
  const handleNameChange = (e : any) => {
    setGoalName(e.target.value);
  }
  
  return (
    <>
    <ReactCardFlip isFlipped={flip} flipDirection="horizontal">

      <Card className="goal" containerProps={{className:'text-center'}}  onClick={newGoalFunction}>
        <CardHeader>
          <h1 > &nbsp;&nbsp;&nbsp;&nbsp;
          {t("Goals.Add Goal")}:
            &nbsp;&nbsp;&nbsp;&nbsp;
          </h1>
        </CardHeader>
        <CardBody>
          <br/>
          <br/>
        <Icon.AddCircle className='usa-icon--size-9 text-mint'/>
        </CardBody>
      </Card>

      <Card className="goal" containerProps={{className:'text-center'}} >
        <Form className="margin-top-neg-2" onSubmit={newFormSubmit}>
          <h2 className="margin-top-2 margin-bottom-0">{t("Goals.New Goal")}</h2>
          <Fieldset >
            <Label className="margin-1 padding-0" htmlFor="title">{t("Goals.Goal Name")}:</Label>
            <TextInput id="title" style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} name="title" type="text" onChange={handleNameChange} value={goalName} required  minLength={3} />

            <Label className="margin-1 padding-0"  htmlFor="goalAmount">{t("Goals.Goal Amount")}:</Label>
            <TextInput id="goalAmount" style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} name="goalAmount" type="number" min={0} value={goalAmount} onChange={handleInputChange} />

            <Label className="margin-1 padding-0" htmlFor="amountSaved">{t("Goals.Amount Already Saved")}:</Label>
            <TextInput id="amountSaved" style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} name="amountSaved" type="number" min={0} value={savedAmount} max={goalAmount} onChange={handleAmountChange}/>

            <Button type="button" style={{borderRadius:"10px"}} onClick={newGoalFunction} className="bg-mint margin-auto">{t("Goals.Cancel")}</Button>
            <Button type="submit" style={{borderRadius:"10px"}} className="bg-mint margin-x-auto">&nbsp;&nbsp;&nbsp;{t("Goals.Add")}&nbsp;&nbsp;&nbsp;</Button>
          </Fieldset>
        </Form>
      </Card>
    </ReactCardFlip>
  </>
  )
}
