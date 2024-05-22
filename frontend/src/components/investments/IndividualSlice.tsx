import { Accordion, Card, CardHeader, CardBody, CardFooter, Button, ButtonGroup, TextInput, Label, Form, ErrorMessage } from "@trussworks/react-uswds";
import ProgressBarSummaries from "../summaries/ProgressBarSummaries";
import { useDeleteInvestmentMutation, useUpdateInvestmentMutation } from "../../app/api/investmentsApi";
import { useState } from "react";
import  { AccordionItemProps } from "@trussworks/react-uswds/lib/components/Accordion/Accordion";
import "../../styles/TaxDeductions.css";
import { useTranslation } from "react-i18next";

{/** To be calculated after obtaining info from AlphaVantage API call */}
interface tempInvestmentData {
    id: string;
    email: string;
    symbol: string;
    name: string;
    quantity: number;
    price: number;
    priceChange: number;
    marketValue: string;
    costBasis: string;
    gainLoss: string;
}

{/** API call to AlphaVantage API. If it's anything but 200, throw an error.
Else, parse necessary info through the JSON object and assign the fields which are relevant */}
export async function getStockData(symbol: string) {

    var url = 'http://localhost:8084/alpha/' + symbol;

    fetch(url)
      .then(response => {
        if (response.status !== 200) {
          console.log('Looks like there was a problem. Status Code: ' +
          response.status);
          return;
        }
        return response.json();
      })
      .then(myJson => {
        console.log(myJson);
        //assignFields(myJson["Global Quote"]["05. price"], myJson["Global Quote"]["09. change"]);
        return;
      })
}


export default function IndividualSlice(props: tempInvestmentData) {
    const [t] = useTranslation();
    ///////delete investment functionality////
    const [deleteInvestment] = useDeleteInvestmentMutation();
    const [quantity, setQuantity] = useState(String(props.quantity));
    const [quantityValid, setQuantityValid] = useState(true);
    const [price, setPrice] = useState(String(Number(props.costBasis) / Number(props.quantity)));
    const [priceValid, setPriceValid] = useState(true);
    function setNewQuantity(quantity: string) {
      if (parseInt(quantity) < 1 || parseInt(quantity) > 1000000) {
        setQuantityValid(false);
        return;
      }
      setQuantityValid(true);
      setQuantity(quantity);
    };
    const setNewPrice = (newPrice: string) => {
      if (parseInt(newPrice) < 0.01 || parseInt(newPrice) > 1000000 ) {
        setPriceValid(false);
        return;
      }
      setPriceValid(true);
      setPrice(newPrice);
    }
    async function deleteHandler() {
      const deleteInvestmentVariables = {
        id: String(props.id),
        email: String(props.email),
        stockName: String(props.name),
        symbol: String(props.symbol),
        shares: Number(props.quantity),
        purchasePrice: Number(props.price)
      };
      
      await deleteInvestment(deleteInvestmentVariables);
      window.location.reload();
    }
    const [isEditActive, setEditActive] = useState(false);

    const [editInvestment] = useUpdateInvestmentMutation();

    async function editSubmit(event: any){
      event.preventDefault();
      const data = new FormData(event.target);
      data.append("new-stock-id", props.id);
      data.append("new-stock-email", props.email);
      data.append("new-stock-name", props.name);
      data.append("new-stock-symbol", props.symbol);
      console.log("in method");
      if(data){
        if(Number.isNaN(Number(data.get('new-stock-purchasePrice')))) {
          setPriceValid(false);
          return;
        }
        const updateInvestmentVariables = {
          id : String(data.get("new-stock-id")),
          email: String(data.get("new-stock-email")),
          stockName: data.get('new-stock-name')?.toString() || '',
          symbol: data.get('new-stock-symbol')?.toString() || '',
          shares: Number(data.get('new-stock-quantity')) || 0,
          purchasePrice: Number(data.get('new-stock-purchasePrice')) || 0,
        };
        try {
          let i = await editInvestment(updateInvestmentVariables);
          editToggle();
          console.log("we tried: " + i);
        } catch(error){
          console.log(error)
        };
      }
      else {
        console.log("no data");
      }
    };

    const accordionItem: AccordionItemProps[] = [ {
      title: props.name + " ( " + props.symbol + " )",
      content:  (           
      <>
      <div style={{display: "flex", justifyContent: "space-between"}}>
        {isEditActive ?
        <>
            <Form onSubmit={editSubmit}>
            <div  style={{paddingRight: "1rem"}}>
            <Label htmlFor="new-stock-quantity">{t("Investments.Number of Shares")}</Label> 
            {quantityValid ? <></> : <ErrorMessage>Please enter a number between 1 and 1,000,000</ErrorMessage>}
            <TextInput id="new-stock-quantity" name="new-stock-quantity" type="number" style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} onChange={(e) => setNewQuantity(e.target.value)} value={quantity} />  
            </div>
            <div  style={{paddingRight: "1rem"}}>
            <Label htmlFor="new-stock-name">{t("Investments.Purchase Price")}</Label> 
            {priceValid ? <></> : <ErrorMessage>Please enter a dollar amount between 1 and 1,000,000</ErrorMessage>}
            <TextInput id="new-stock-purchasePrice" name="new-stock-purchasePrice" type="text" style={{borderRadius:"10px", boxShadow:"0px 4px 6px rgba(0, 0, 0, 0.2)"}} onChange={(e) => setNewPrice(e.target.value)} value={price} />        
            </div>  
            <Button type="submit" className="bg-mint margin-auto-x" style={{ borderRadius: "10px" }}>{t("Investments.Confirm Edit")}</Button>
            </Form>                                             
        </> : 
        <>
        <div><b>{t("Investments.Symbol")}</b><br/><div id = "symbol">{props.symbol}</div></div>
        <div><b>{t("Investments.Name")}</b><br/><div id = "name">{props.name}</div></div>
        <div><b>Qty.</b><br/><div id = "shares">{props.quantity}</div></div>
        <div><b>{t("Investments.Price")}</b><br/><div id = "price">${props.price}</div></div>
        <div><b>{t("Investments.Price Change")}</b><br/><div id = "price-change">{props.priceChange}</div></div>
        <div><b>{t("Investments.Market Value")}</b><br/><div id = "market-value">${props.marketValue}</div></div>
        <div><b>{t("Investments.Cost Basis")}</b><br/><div id = "cost-basis">${props.costBasis}</div></div>
        <div><b>{t("Investments.Gain Loss")}</b><br/><div id = "gain-loss">${props.gainLoss}</div></div>
        </>
        }
        <ButtonGroup type="default" style={{display: "block"}}>
          {isEditActive ?  
          <></>
          :
          <Button type="button" onClick={() =>editToggle()} style={{backgroundColor: "#04c585", borderRadius: "10px"}}>&nbsp;&nbsp;&nbsp;{t("Investments.Edit")}&nbsp;&nbsp;&nbsp;</Button>
          }
          {isEditActive ?
          <Button type="button" onClick={() =>editToggle()} style={{backgroundColor: "rgb(172, 172, 172)", borderRadius: "10px"}}>{t("Investments.Cancel")}</Button>
          :
          <></>
          }
          <Button type="button" onClick={() =>deleteHandler()} style={{backgroundColor: "rgb(172, 172, 172)", borderRadius: "10px"}}>{t("Investments.Delete")}&nbsp;</Button>
        </ButtonGroup>
      </div>
      </>
      ),
      expanded: false,
      id: props.id,
      headingLevel: "h1"

    } ]
    

    const editToggle = () => {
      setEditActive(!isEditActive);
    };
    

    return (
      <>
      <Accordion bordered={true} items = {accordionItem} className="custom-accordian"></Accordion>
      {/*
        <Card
          layout="flagMediaRight"
          gridLayout={{ tablet: { col: 12 } }}
          containerProps={{ className: "border-ink" }}
        >
          <CardBody style={{marginRight: "0rem", marginLeft: "0rem", paddingLeft: "1rem", paddingRight: "1rem"}}>           
            <>
            <div style={{display: "flex", justifyContent: "space-between"}}>
              {isEditActive ?
              <>
              <div  style={{paddingRight: "1rem"}}>
              <Label htmlFor="new-stock-name">Symbol</Label>
              <TextInput id="new-stock-symbol" name="new-stock-symbol" type="text" value={props.symbol}/>   
              </div>
              <div style={{paddingRight: "1rem"}}>
              <Label htmlFor="new-stock-name">Name</Label>
              <TextInput id="new-stock-name" name="new-stock-name"type="text" value={props.name}/> 
              </div>
              <div  style={{paddingRight: "1rem"}}>
              <Label htmlFor="new-stock-quantity">Number of Shares</Label> 
              <TextInput id="new-stock-quantity" name="new-stock-quantity" type="text" />  
              </div>
              <div  style={{paddingRight: "1rem"}}>
              <Label htmlFor="new-stock-name">Purchase Price</Label> 
              <TextInput id="new-stock-purchasePrice" name="new-stock-purchasePrice" type="text" />        
              </div>                                           
              </> : 
              <>
              <div><b>Symbol</b><br/><div id = "symbol">{props.symbol}</div></div>
              <div><b>Name</b><br/><div id = "name">{props.name}</div></div>
              <div><b>Qty.</b><br/><div id = "shares">{props.quantity}</div></div>
              <div><b>Price</b><br/><div id = "price">${props.price}</div></div>
              <div><b>Price Change</b><br/><div id = "price-change">{props.priceChange}</div></div>
              <div><b>Market Value</b><br/><div id = "market-value">${props.marketValue}</div></div>
              <div><b>Cost Basis</b><br/><div id = "cost-basis">${props.costBasis}</div></div>
              <div><b>Gain Loss</b><br/><div id = "gain-loss">${props.gainLoss}</div></div>
              </>
              }
              <ButtonGroup type="default" style={{display: "block"}}>
                {isEditActive ?  
                <Button type="button" onClick={() =>editSubmit()} style={{backgroundColor: "#04c585"}}>Confirm</Button>
                :
                <Button type="button" onClick={() =>editToggle()} style={{backgroundColor: "#04c585"}}>Edit</Button>
                }
                {isEditActive ?
                <Button type="button" onClick={() =>editToggle()} style={{backgroundColor: "rgb(172, 172, 172)"}}>X</Button>
                :
                <></>
                }
                <Button type="button" onClick={() =>deleteHandler()} style={{backgroundColor: "rgb(172, 172, 172)"}}>Delete</Button>
              </ButtonGroup>
            </div>
            </>
          </CardBody>
        </Card>
              */}
      </>
    );
  }