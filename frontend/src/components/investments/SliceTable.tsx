import { Accordion, Button, ButtonGroup, Card, CardBody, CardHeader, Form, Label, TextInput } from "@trussworks/react-uswds";
import React, { useEffect } from "react";
import { useState } from "react";
import GoalIndividualCard from "../summaries/GoalIndividualCard";
import IndividualSlice from "./IndividualSlice";
import { AccordionItem, AccordionItemProps } from "@trussworks/react-uswds/lib/components/Accordion/Accordion";
import { useDeleteInvestmentMutation, useGetInvestmentsByEmailQuery, useUpdateInvestmentMutation } from "../../app/api/investmentsApi";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../../app/features/userSlice";
import "../../styles/investmentCard.css"
import "../../styles/TaxDeductions.css";
import { useTranslation } from "react-i18next";



interface tempInvestmentData {
  id: string;
  email: string;
  symbol: string;
  name: string;
  quantity: number;
  purchasePrice: number;
  price: number;
  priceChange: number;
  marketValue: string;
  costBasis: string;
  gainLoss: string;
}

export default function SliceTable(props: {
    tempInvestmentData: tempInvestmentData[];
    symbol: string;
  }) {
    const [t] = useTranslation();
    const [array, setArray] = useState<AccordionItemProps[]>([]);
    const userEmail = useSelector(selectUserEmail);
    const [isDeleted, setIsDeleted] = useState(true);
    const [isEditActive, setEditActive] = useState(false);
    console.log(isEditActive);
    const editToggle = () => {
      setEditActive(!isEditActive);
    }
    console.log("String: " + props.symbol);
    useEffect(() => {
      const tempArray: AccordionItemProps[] = props.tempInvestmentData.map((investment) => ({
        title: investment.name + " ( " + investment.symbol + " ) ",
        content: (
            <div style={{display: "flex", justifyContent: "space-between"}}>
            {isEditActive ?
            <>
            <Form onSubmit={editHandler}>
            <div  style={{paddingRight: "1rem"}}>
            <Label htmlFor="new-stock-id">ID</Label>
            <TextInput id="new-stock-id"name="new-stock-id" type="text" value={investment.id} onChange={() => {}}/>   
            </div>
            <div  style={{paddingRight: "1rem"}}>
            <Label htmlFor="new-stock-email">Email</Label>
            <TextInput id="new-stock-email" name="new-stock-email" type="text" value={investment.email} onChange={() => {}}/>   
            </div>
            <div  style={{paddingRight: "1rem"}}>
            <Label htmlFor="new-stock-symbol">{t("Investments.Symbol")}</Label>
            <TextInput id="new-stock-symbol" name="new-stock-symbol" type="text" value={investment.symbol}  onChange={() => {}}/>   
            </div>
            <div style={{paddingRight: "1rem"}}>
            <Label htmlFor="new-stock-name">{t("Investments.Name")}</Label>
            <TextInput id="new-stock-name" name="new-stock-name"type="text" value={investment.name}  onChange={() => {}}/> 
            </div>
            <div  style={{paddingRight: "1rem"}}>
            <Label htmlFor="new-stock-quantity">{t("Investments.Number of Shares")}</Label> 
            <TextInput id="new-stock-quantity" name="new-stock-quantity" type="text" />  
            </div>
            <div  style={{paddingRight: "1rem"}}>
            <Label htmlFor="new-stock-name">{t("Investments.Purchase Price")}</Label> 
            <TextInput id="new-stock-purchasePrice" name="new-stock-purchasePrice" type="text" />        
            </div>  
            <Button type="submit" className="bg-mint margin-auto-x">{t("Investments.Confirm Edit")}</Button>
            </Form>      
            </> : 
            <>
            <div><b>{t("Investments.Symbol")}</b><br/><div id = "symbol">{investment.symbol}</div></div>
            <div><b>{t("Investments.Name")}</b><br/><div id = "name">{investment.name}</div></div>
            <div><b>Qty.</b><br/><div id = "shares">{investment.quantity}</div></div>
            <div><b>{t("Investments.Price")}</b><br/><div id = "price">${investment.price}</div></div>
            <div><b>{t("Investments.Price Change")}</b><br/><div id = "price-change">{investment.priceChange}</div></div>
            <div><b>{t("Investments.Market Value")}</b><br/><div id = "market-value">${investment.marketValue}</div></div>
            <div><b>{t("Investments.Cost Basis")}</b><br/><div id = "cost-basis">${investment.costBasis}</div></div>
            <div><b>{t("Investments.Gain Loss")}</b><br/><div id = "gain-loss">${investment.gainLoss}</div></div>
            </>
            }
  
            <ButtonGroup type="default" style={{display: "block"}}>
              <Button type="button" onClick={editToggle} style={{backgroundColor: "#04c585"}}>Edit</Button>
              <Button type="button" onClick={() =>deleteHandler(investment.id, investment.email, investment.name, investment.symbol, investment.quantity, investment.purchasePrice)} style={{backgroundColor: "rgb(172, 172, 172)"}}>Delete</Button>
            </ButtonGroup>
            </div>
        ),
        expanded: false,
        id: investment.id,
        headingLevel: 'h1',
        handleToggle: (event) => {
          // Define your toggle logic here
        },
      }));
      setArray(tempArray);
    },[props.symbol])

    const [deleteInvestment] = useDeleteInvestmentMutation();
  
    function deleteHandler(id: string, email: string, stockName: string, symbol: string, shares: number, purchasePrice: number) {
      const deleteInvestmentVariables = {
        id: String(id),
        email: String(email),
        stockName: String(stockName),
        symbol: String(symbol),
        shares: Number(shares),
        purchasePrice: Number(purchasePrice)
      };
      setIsDeleted(true);
      deleteInvestment(deleteInvestmentVariables);
    }
    const [editInvestment] = useUpdateInvestmentMutation();
    
    async function editHandler(event: any) {
      event.preventDefault();
      const data = new FormData(event.target);
      console.log("in method");
      if(data){
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
    }
    return (
      <Card
        headerFirst
        gridLayout={{ desktop: { col: "fill" } }}
        containerProps={{
        }}
        className="listInvestments"
      >
        <CardHeader className="bg-lightest text-left">
          <h2 className="bold">{t("Investments.Investments")}</h2>
        </CardHeader>
        <CardBody className="padding-top-3 margin-x-0">
          <ul style={{ listStyleType: "none", paddingLeft: "0px" }}>
            {/*<Accordion bordered = {true} multiselectable = {true} items={array} className="custom-accordion"></Accordion>*/}
            {props.tempInvestmentData.map((stockData: tempInvestmentData, index: number) => {
              return (
                <React.Fragment key={index}>
                  <IndividualSlice 
                      id= {stockData.id}
                      email= {stockData.email}
                      symbol= {stockData.symbol}
                      name= {stockData.name}
                      quantity= {stockData.quantity}
                      price= {stockData.price}
                      priceChange= {stockData.priceChange}
                      marketValue= {stockData.marketValue}
                      costBasis= {stockData.costBasis}
                      gainLoss= {stockData.gainLoss}
                  />
                </React.Fragment>
              );
            })}
          </ul>
        </CardBody>
        {/* <CardFooter>
          if needed footer goes here
        </CardFooter> */}
      </Card>
    );
  }