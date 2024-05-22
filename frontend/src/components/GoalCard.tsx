import { Alert, Button, Card, CardBody, CardFooter, CardGroup, CardHeader, Fieldset, Form, Label, SiteAlert, TextInput } from '@trussworks/react-uswds'
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import ProgressBar from './ProgressBar';
import { useUpdateGoalMutation } from '../app/api/goalsApi';
import { useDeleteGoalMutation } from '../app/api/goalsApi';
import ReactCardFlip from "react-card-flip";
import "../styles/goalCard.css"




interface GoalCardProps {
  _id : string
  email : string;
  name: string;
  goalAmount: number;
  amountSaved: number;
}

export default function GoalCard(props: GoalCardProps) {

  const [flip, setFlip] = useState(false);

  const {t} = useTranslation();

  
  //Calculate progress
  const interval = 100;
  let [progress, setProgress] = useState(Math.floor((props.amountSaved/props.goalAmount)*interval))
  ////////////////////

  
  ///////delete goal functionality////
  const [deleteGoal] = useDeleteGoalMutation();
  
  function deleteHandler() {
    const deleteGoalVariables = {
      _id: props._id,
      email: String(props.email),
      name: String(props.name),
      goalAmount: Number(props.goalAmount),
      amountSaved: Number(props.amountSaved)
    };
    
    deleteGoal(deleteGoalVariables);
  }
  //////////////////////////////////
  
  //State for updating form to render
  // const [updateGoal, setUpdateGoal] = useState(false)
  // //updating Goal state
  function updateHandler(){
    setFlip(!flip)
    
  }
  
  
  //updating the goal//////
  const [updateGoalMutation, mutationResult] =  useUpdateGoalMutation();
  async function updateFormSubmit(event : any){
    event.preventDefault();
    const data = new FormData(event.target);
    
    if(data){
      const updateGoalVariables = {
        _id : (props._id),
        email: String(props.email),
        name: data.get('title')?.toString() || '',
        goalAmount: Number(data.get('goalAmount')) || 0,
        amountSaved: Number(data.get('amountSaved')) || 0,
      };
      try {
        await updateGoalMutation(updateGoalVariables);
        updateHandler()
        setProgress(Math.floor((updateGoalVariables.amountSaved/updateGoalVariables.goalAmount)*interval))
      } catch(error){
        console.log(error)
      };
    }
  }
  ///////////////////

  //used to get form changes for the GoalAmount
  const [goalAmount, setGoalAmount] = useState(props.goalAmount);
  const handleInputChange = (e : any) => {
    setGoalAmount(e.target.value);
  }
  ///////////////////

//Render the Goal   

 return (
   <>
    <ReactCardFlip isFlipped={flip} flipDirection="horizontal">
      <Card className="goal" containerProps={{className:'text-center'}} >
        <CardHeader>
          <h2>
            {props.name}
          </h2>
        </CardHeader>
        <CardBody>
          <h3 className="margin-top-2 margin-bottom-4">{t("Goals.Goal Amount")}: <span className='text-mint'>$ {props.goalAmount}</span></h3>
          <h3 className="margin-top-2 margin-bottom-4">{t("Goals.Amount Saved")}: <span className='text-mint'>$ {props.amountSaved}</span></h3>
          <ProgressBar name={props.name} goalAmount={props.goalAmount} amountSaved={props.amountSaved} progress={progress} />
        </CardBody>
        <CardFooter className="margin-top-2 margin-bottom-0">
          <Button type="button" style={{borderRadius:"10px"}} onClick={updateHandler} className="bg-mint margin-auto">{t("Goals.Update")}</Button>
          <Button type="button" style={{borderRadius:"10px"}} onClick={deleteHandler} className="bg-mint margin-x-auto">{t("Goals.Delete")}</Button>
        </CardFooter>
      </Card>
    
      <Card className="goal" containerProps={{className:'text-center'}}>
        <Form className="margin-top-neg-2" onSubmit={updateFormSubmit}>
         <h2 className="margin-top-2 margin-bottom-0">{t("Goals.Update Goal")}</h2>
         <Fieldset>
          <Label className="margin-1 padding-0" htmlFor="title">{t("Goals.Goal Name")}:</Label>
          <TextInput id="title" style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} name="title" type="text" required  minLength={3} defaultValue={props.name}/>

          <Label className="margin-1 padding-0"  htmlFor="goalAmount">{t("Goals.Goal Amount")}:</Label>
          <TextInput id="goalAmount" style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} name="goalAmount" type="number" min={0} onChange={handleInputChange} defaultValue={props.goalAmount}/>

          <Label className="margin-1 padding-0" htmlFor="amountSaved">{t("Goals.Amount Saved")}:</Label>
          <TextInput id="amountSaved" style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} name="amountSaved" type="number" min={0} max={goalAmount} defaultValue={(props.amountSaved)} />

          <Button type="submit" style={{borderRadius:"10px"}} className="bg-mint margin-auto">{t("Goals.Update")}</Button>
          <Button type="button" style={{borderRadius:"10px"}} onClick={updateHandler} className="bg-mint margin-auto-x">{t("Goals.Cancel")}</Button>
         </Fieldset>
        </Form>
      </Card>
    </ReactCardFlip>
  </>
 )
}
 