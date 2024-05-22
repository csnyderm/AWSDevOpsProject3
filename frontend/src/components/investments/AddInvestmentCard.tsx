import { Card, CardHeader, CardBody, ButtonGroup, Button, ModalRef, Modal, ModalFooter, ModalHeading, ModalToggleButton, Fieldset, Form, Label, TextInput, FormGroup, ErrorMessage } from "@trussworks/react-uswds";
import React, { useRef, useState } from "react";
import IndividualSlice from "./IndividualSlice";
import { useAddInvestmentMutation } from "../../app/api/investmentsApi";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../../app/features/userSlice";
import "../../styles/investmentCard.css"
import { useTranslation } from "react-i18next";
interface tempSliceData {
    email: string;
    stockName: string;
    symbol: string;
    shares: number;
    purchasePrice: number;
}

export default function AddInvestmentCard() {

    const { t } = useTranslation();

    {/** Swap this userEmail in for the dummy one  */}
    
    const userEmail = useSelector(selectUserEmail).toString();

    const [addInvestmentMutation, mutationResult] = useAddInvestmentMutation();

    const [stockName, setName] = useState("")
    const [symbol, setSymbol] = useState("")
    const [shares, setShares] = useState("")
    const [sharesValid, setSharesValid] = useState(true);
    const [price, setPrice] = useState("")
    const [priceValid, setPriceValid] = useState(true);


    const setNewShares = (newShares: string) => {

    if (parseInt(newShares) < 1 || parseInt(newShares) > 1000000) {
        setSharesValid(false);
        return;
      }
      setSharesValid(true);
      setShares(newShares);
    };
    const setNewPrice = (newPrice: string) => {
      if (parseInt(newPrice) < 0.01 || parseInt(newPrice) > 1000000 ) {
        setPriceValid(false);
        return;
      }
      setPriceValid(true);
      setPrice(newPrice);
    }
    async function newInvestmentSubmit(event : any){
      event.preventDefault();
      const data = new FormData(event.target);
      if(data){
        if(Number.isNaN(Number(data.get('stock-price')))) {
          setPriceValid(false);
          return;
        }
        const newInvestmentVariables = {
          email: String(userEmail),
          stockName: data.get('stock-name')?.toString() || '',
          symbol: data.get('stock-symbol')?.toString() || '',
          shares: Number(data.get('stock-qty')) || 0,
          purchasePrice: Number(data.get('stock-price')) || 0,
        };
        try {
          await addInvestmentMutation(newInvestmentVariables);
        } catch(error){
          console.log("something wrong");
        } 
      }
      ResetForm();
    }

    function ResetForm() {
      setName("");
      setSymbol("");
      setPrice("");
      setShares("");
    }
    const [isInputVisible, setIsInputVisible] = useState(false);

    const handleClick = () => {
      setIsInputVisible(!isInputVisible);
    };
    return (
      <Card
        headerFirst
        gridLayout={{ desktop: { col: "fill" } }}
        containerProps={{
        }}
        className="addInvestment"
      >
        <CardHeader className="bg-lightest text-left">
          <h2 className="bold">{t("Investments.Actions")}</h2>
        </CardHeader>
        <CardBody>
                {/** When the add button is clicked initially, all of these inputs will render,
                 * the text will become 'Cancel', and the button will turn gray
                 */ }
                {isInputVisible ? (
                  <>
                    <Label htmlFor="stock-info">{t("Investments.Please Enter Stock Information")}</Label>
                    <Form onSubmit={newInvestmentSubmit}>
                      <Label htmlFor="stock-name">{t("Investments.Stock Name")}</Label>
                        <TextInput required style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} id="stock-name" name="stock-name" type="text" onChange={(e) => setName(e.target.value)} value={stockName}/>
                      <Label htmlFor="stock-symbol">{t("Investments.Stock Symbol")}</Label>
                        <TextInput required maxLength={5} style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} id="stock-symbol" name="stock-symbol" type="text"  onChange={(e) => setSymbol(e.target.value)} value={symbol}/>
                      <Label htmlFor="stock-name">{t("Investments.Number of Shares")}</Label>
                      {sharesValid ? <></> : <ErrorMessage>Please enter a number between 1 and 1,000,000</ErrorMessage>}
                        <TextInput required style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} id="stock-qty" name="stock-qty" type="number"  onChange={(e) => setNewShares(e.target.value)} value={shares}/>
                      <Label htmlFor="stock-price">{t("Investments.Purchase Price")}</Label>
                      {priceValid ? <></> : <ErrorMessage>Please enter a dollar amount between $.01 and $1,000,000.00</ErrorMessage>}
                        <TextInput required style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} id="stock-price" name="stock-price" type="text"  onChange={(e) => setNewPrice(e.target.value)} value={price}/>
                   
                    <ButtonGroup className="flex-justify-left" >
                    <Button type="submit" className="bg-mint" style={{ borderRadius: "10px" }} >
                    &nbsp;&nbsp;&nbsp;{t("Investments.Add")}&nbsp;&nbsp;&nbsp;
                    </Button>
                    <Button type="button"  style={isInputVisible ? { backgroundColor: "rgb(172, 172, 172)", color: "#FFF", boxShadow: "#04c585", borderRadius: "10px"} : { backgroundColor: "#04c585", color: "#FFF", boxShadow: "#04c585", borderRadius: "10px"}} onClick={handleClick} >
                  {isInputVisible ?  t("Investments.Cancel"): t("Investments.Add")}
                </Button>
                    </ButtonGroup>
                  <div id="message"></div>
                  </Form>
                  </>
                ) :
                <ButtonGroup className="flex-justify-left" >

                <Button type="button" size="big"  style={isInputVisible ? { backgroundColor: "rgb(172, 172, 172)", color: "#FFF", boxShadow: "#04c585", borderRadius: "10px"} : { backgroundColor: "#04c585", color: "#FFF", boxShadow: "#04c585", borderRadius: "10px"}} onClick={handleClick} >
                  {!isInputVisible ? t("Investments.Add"): t("Investments.Cancel")}
                </Button>
                </ButtonGroup>
}
        </CardBody>
        {/* <CardFooter>
          if needed footer goes here
        </CardFooter> */}
      </Card>
    );
  }
