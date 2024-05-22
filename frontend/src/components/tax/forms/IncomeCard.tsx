import { Button, ButtonGroup, Card, CardBody, CardFooter, CardGroup, CardHeader, CardMedia, Fieldset, Form, Grid, GridContainer, Label, Modal, ModalFooter, ModalHeading, ModalToggleButton, TextInput, Title } from "@trussworks/react-uswds";
import "../../../styles/IncomeCards.css";
import { useRef, useState } from "react";
import ReactCardFlip from "react-card-flip";
import { useDeleteIncomeSourceByEmailAndEINMutation, useUpdateTaxReturnIncomeSourceMutation } from "../../../app/api/taxApi";
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../../../app/features/userSlice";
import { useTranslation } from "react-i18next";

interface IncomeCardProps {
    empID: number;
    incomeType: string;
    state: string;
    income: number;
    fedWithheld: number;
    stateWithheld: number;
  }

export default function IncomeCard(props: IncomeCardProps) {
    
    const { t } = useTranslation();

    const modalRef = useRef(null);
    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; // temporary

    const [flip, setFlip] = useState(false);

    // DELETE Income Card
    const [deleteIncomeCard] = useDeleteIncomeSourceByEmailAndEINMutation();

    function HandleDelete() {

        const deletedIncomeCard = {
            email: userEmail,
            ein: Number(props.empID),
        };

        deleteIncomeCard(deletedIncomeCard);
    }


    // UPDATE Income Card
    const [updateIncomeSource, updateIncomeSourceResult] = useUpdateTaxReturnIncomeSourceMutation();

    const [editedIncomeSource, setEditedIncomeSource] = useState({
        empID : props.empID ,
        incomeType : props.incomeType,
        state  : props.state,
        income : props.income,
        fedWithheld : props.fedWithheld,
        stateWithheld   : props.stateWithheld,
    });

    const handleEditInputChange = (event: React.ChangeEvent<HTMLInputElement>, fieldName: keyof IncomeCardProps) => {
        const { value } = event.target;
        setEditedIncomeSource((prevData) => ({
            ...prevData,
            [fieldName]:value,
        }));
        
        const numberValue = Number(value);

        if (event.target.name === "empID") {
            editedIncomeSource.empID = numberValue;
            } else if (event.target.name === "stateWithheld") {
            editedIncomeSource.stateWithheld = numberValue;
            } else if (event.target.name === "fedWithheld") {
            editedIncomeSource.fedWithheld = numberValue;
            } else if (event.target.name === "state") {
            editedIncomeSource.state = value;
        }
          
          handleEditSave();
    };

    const handleEditSave = async () => {

        
        try {
            const response = await updateIncomeSource({
                email: userEmail,
                updatedIncomeSource: editedIncomeSource,
            });
    
        } catch (error) {
            console.error("Error updating income source: ", error);
        }
    };

    const handleCancel = () => {
        // Reset the editedIncomeSource to its original values
        setEditedIncomeSource({
            empID: props.empID,
            incomeType: props.incomeType,
            state: props.state,
            income: props.income,
            fedWithheld: props.fedWithheld,
            stateWithheld: props.stateWithheld,
        });
    
        // Close the modal
        setFlip(false);
    };

    return (
        <div className="income-card">

                <ReactCardFlip isFlipped={flip} flipDirection="horizontal">

                    <Card headerFirst className="frontSide" >
                        <CardHeader>
                            <Button type="button"  onClick={HandleDelete} className="usa-card__heading usa-button--outline"><img src="src\assets\delete_icon.png" /></Button>
                        </CardHeader>
                        <CardMedia imageClass="add-aspect-1x1 width-card">
                            {}
                            <img
                            src="src\assets\forms_icon.png"
                            alt="An image's description"
                            className="pin-all"
                            onClick={() => setFlip(!flip)}
                            />
                        </CardMedia>
                        <CardBody>
                            <h3 style={{textAlign: "center"}}>{props.incomeType}{" " + t("taxIncomePage.form")}</h3>
                        </CardBody>
                        <CardFooter><br/></CardFooter>
                    </Card>

                    <Card headerFirst className="backSide">
                        <CardHeader className="display-flex flex-row">
                        <Button type="button" onClick={HandleDelete} className="usa-card__heading usa-button--outline">
                            <img src="src\assets\delete_icon.png" />
                        </Button>
                            <h2 style={{textAlign: "center"}}>{props.incomeType}{" " + t("taxIncomePage.form")}</h2>
                        </CardHeader>
                        <CardBody style={{textAlign: "center"}} onClick={() => setFlip(!flip)} >
                            <div>
                                <h4>{t("taxIncomePage.income") + " : $"}<span className='text-mint'>{props.income}</span></h4>
                                <h4>{t("taxIncomePage.stateTaxWithheld") + " : $"}<span className='text-mint'>{props.stateWithheld}</span></h4>
                                <h4>{t("taxIncomePage.federalTaxWithheld") + " : $"}<span className='text-mint'>{props.fedWithheld}</span></h4>
                                <h4>{t("taxIncomePage.state") + " : "}<span className='text-mint'>{props.state}</span></h4>
                            </div>
                        </CardBody>
                        <CardFooter className="display-flex flex-column flex-align-end">
                            <ModalToggleButton modalRef={modalRef} opener>
                                <img  src="src/assets/edit_icon.png"/>
                            </ModalToggleButton>
                            <Modal 
                                ref={modalRef}
                                id="example-modal-1"
                                aria-labelledby="modal-1-heading"
                                aria-describedby="modal-1-description"
                            >
                                <Form onSubmit={handleEditSave}>
                                    <Fieldset legend={t("taxIncomePage.edit") + " " + t("taxIncomePage.form")} legendStyle="large" style={{ minWidth: "25vw" }}>
                                        <Label htmlFor="empID">{t("taxIncomePage.EIN")}</Label>
                                        <TextInput
                                            id="empID" 
                                            name="empID" 
                                            type="text" 
                                            placeholder="$" 
                                            value={editedIncomeSource.empID}
                                            onChange={(event) => handleEditInputChange(event, "empID")}
                                            style={{maxWidth: "15rem"}}
                                            />
                                        <Label htmlFor="income">{t("taxIncomePage.income")}</Label>
                                        <TextInput
                                            id="income" 
                                            name="income" 
                                            type="text" 
                                            placeholder="$" 
                                            value={editedIncomeSource.income}
                                            onChange={(event) => handleEditInputChange(event, "income")}
                                            style={{maxWidth: "15rem"}}
                                            />
                                        <Label htmlFor="stateWithheld">{t("taxIncomePage.stateTaxWithheld")}</Label>
                                        <TextInput
                                            id="stateWithheld" 
                                            name="stateWithheld" 
                                            type="text" 
                                            placeholder="$" 
                                            value={editedIncomeSource.stateWithheld}
                                            onChange={(event) => handleEditInputChange(event, "stateWithheld")}
                                            style={{maxWidth: "15rem"}}
                                            />
                                        <Label htmlFor="fedWithheld">{t("taxIncomePage.federalTaxWithheld")}</Label>
                                        <TextInput
                                            id="fedWithheld" 
                                            name="fedWithheld" 
                                            type="text" 
                                            placeholder="$" 
                                            value={editedIncomeSource.fedWithheld}
                                            onChange={(event) => handleEditInputChange(event, "fedWithheld")}
                                            style={{maxWidth: "15rem"}}
                                            />
                                        <Label htmlFor="state">{t("taxIncomePage.state")}</Label>
                                        <TextInput
                                            id="state" 
                                            name="state" 
                                            type="text" 
                                            placeholder="$" 
                                            value={editedIncomeSource.state}
                                            onChange={(event) => handleEditInputChange(event, "state")}
                                            style={{maxWidth: "15rem"}}
                                            />
                                    </Fieldset>
                                </Form>
                                <ModalFooter>
                                <ButtonGroup>
                                    <ModalToggleButton className="bg-mint text-white" modalRef={modalRef} onSubmit={handleEditSave} closer>
                                    {t("taxIncomePage.save")}
                                    </ModalToggleButton>
                                    <ModalToggleButton
                                        modalRef={modalRef}
                                        closer
                                        unstyled
                                        className="padding-105 text-center text-no-underline"
                                        onClick={handleCancel}    
                                    >
                                        {t("taxIncomePage.goBack")}
                                    </ModalToggleButton>
                                    </ButtonGroup>
                                </ModalFooter>
                                
                            </Modal>
                            {/* <Button type="button" className="usa-button--outline editButton" onClick={handleEditInputChange}>
                                <img src="src\assets\edit_icon.png" />
                            </Button> */}
                        </CardFooter>
                    </Card>

                </ReactCardFlip>

        </div>
    )
}