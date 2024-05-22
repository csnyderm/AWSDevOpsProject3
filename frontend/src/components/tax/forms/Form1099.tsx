import {
  Button,
  Dropdown,
  Fieldset,
  Form,
  Label,
  TextInput,
} from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import {
  IncomeSource,
  useFindOneQuery,
  useUpdateTaxReturnMutation,
} from "../../../app/api/taxApi";
import { useState } from "react";
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../../../app/features/userSlice";

export default function Form1099({ onClose }: { onClose: () => void }) {
  const { t } = useTranslation();

  const userEmail = useSelector(selectUserEmail);
  //const userEmail: string = "testingNewModel@gmail.com"; // temporary
  const { data: taxReturnData, isLoading } = useFindOneQuery(userEmail);
  const [updateTaxReturn] = useUpdateTaxReturnMutation();

  const [formData, setFormData] = useState({
    empID: 0,
    incomeType: "1099",
    state: "",
    income: 0,
    fedWithheld: 0,
    stateWithheld: 0,
  });

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const newIncomeSource: IncomeSource = {
      empID: formData["empID"],
      incomeType: "1099",
      state: formData["state"],
      income: formData["income"],
      fedWithheld: formData["fedWithheld"],
      stateWithheld: formData["stateWithheld"],
    };

    if (!isLoading && taxReturnData) {
      const newIncomeSourceArray = [
        ...taxReturnData.incomeSources,
        newIncomeSource,
      ];

      const updatedTaxReturn = {
        ...taxReturnData,
        incomeSources: newIncomeSourceArray,
      };
      console.log("1099 post");
      updateTaxReturn(updatedTaxReturn);
    }

    onClose();
  };

  // async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
  //     event.preventDefault();

  //     const newIncomeSource: IncomeSource = {
  //         empID: formData['empID'],
  //         type: '1099',
  //         state: formData['state'],
  //         income: formData['income'],
  //         fedWithheld: formData['fedWithheld'],
  //         stateWithheld: formData['stateWithheld']
  //     };
  //     // first need to update the income source data to get the empID
  //     await updateIncomeSource({ email: userEmail, updatedIncomeSource: newIncomeSource })

  //     // only when empID is submitted, fetch the updated income data after mutation

  //     onClose();
  // };

  // const { data: updatedIncomeData } = useFindIncomeSourceQuery({ email: userEmail, ein: newIncomeSource.empID });

    function handleInputChange(event: React.ChangeEvent<HTMLInputElement>) {
        const { name, value } = event.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };
    
    function handleDropdownChange(event: React.ChangeEvent<HTMLSelectElement>) {
        const { name, value } = event.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };
    
    return (
        <>
            <Form onSubmit={handleSubmit}>
                <Fieldset legend={"1099 " + t("taxIncomePage.form")} legendStyle="large" style={{ minWidth: "25vw" }}>

                <div>
                        <Label htmlFor="ein">{t("taxIncomePage.EIN")}{' '}
                            <abbr title="required" className="usa-hint usa-hint--required">
                            *
                            </abbr>
                        </Label>
                        <span className="usa-hint">{t("taxIncomePage.forExample") + "123456789"}</span>
                        <TextInput 
                            id="empID" 
                            name="empID" 
                            type="text" 
                            value={formData['empID']}
                            onChange={handleInputChange}
                            style={{maxWidth: "15rem"}}
                            required
                            />
                    </div>
                    <div>
                        <Label htmlFor="income">{t("taxIncomePage.income")}{' '}
                        <abbr title="required" className="usa-hint usa-hint--required">
                        *
                        </abbr></Label>
                        <span className="usa-hint">{t("taxIncomePage.inputValidNumber")}</span>
                        <TextInput 
                            id="income" 
                            name="income" 
                            type="text" 
                            placeholder="$" 
                            value={formData['income']}
                            onChange={handleInputChange}
                            style={{maxWidth: "15rem"}}
                            required
                            />
                    </div>
                    <div>
                        <Label htmlFor="state">
                        {t("taxIncomePage.state")}{' '}
                                <abbr title="required" className="usa-hint usa-hint--required">
                                *
                                </abbr>
                        </Label>
                        <Dropdown 
                            id="state" 
                            name="state" 
                            value={formData['state']}
                            onChange={handleDropdownChange}
                            style={{maxWidth: "15rem"}}
                            required>
                            <option>- {t("select", {ns: ['main', 'home']})} -</option>
                            <option value="AL">Alabama</option>
                            <option value="AK">Alaska</option>
                            <option value="AZ">Arizona</option>
                            <option value="AR">Arkansas</option>
                            <option value="CA">California</option>
                            <option value="CO">Colorado</option>
                            <option value="CT">Connecticut</option>
                            <option value="DE">Delaware</option>
                            <option value="DC">District of Columbia</option>
                            <option value="FL">Florida</option>
                            <option value="GA">Georgia</option>
                            <option value="HI">Hawaii</option>
                            <option value="ID">Idaho</option>
                            <option value="IL">Illinois</option>
                            <option value="IN">Indiana</option>
                            <option value="IA">Iowa</option>
                            <option value="KS">Kansas</option>
                            <option value="KY">Kentucky</option>
                            <option value="LA">Louisiana</option>
                            <option value="ME">Maine</option>
                            <option value="MD">Maryland</option>
                            <option value="MA">Massachusetts</option>
                            <option value="MI">Michigan</option>
                            <option value="MN">Minnesota</option>
                            <option value="MS">Mississippi</option>
                            <option value="MO">Missouri</option>
                            <option value="MT">Montana</option>
                            <option value="NE">Nebraska</option>
                            <option value="NV">Nevada</option>
                            <option value="NH">New Hampshire</option>
                            <option value="NJ">New Jersey</option>
                            <option value="NM">New Mexico</option>
                            <option value="NY">New York</option>
                            <option value="NC">North Carolina</option>
                            <option value="ND">North Dakota</option>
                            <option value="OH">Ohio</option>
                            <option value="OK">Oklahoma</option>
                            <option value="OR">Oregon</option>
                            <option value="PA">Pennsylvania</option>
                            <option value="RI">Rhode Island</option>
                            <option value="SC">South Carolina</option>
                            <option value="SD">South Dakota</option>
                            <option value="TN">Tennessee</option>
                            <option value="TX">Texas</option>
                            <option value="UT">Utah</option>
                            <option value="VT">Vermont</option>
                            <option value="VA">Virginia</option>
                            <option value="WA">Washington</option>
                            <option value="WV">West Virginia</option>
                            <option value="WI">Wisconsin</option>
                            <option value="WY">Wyoming</option>
                        </Dropdown>
                    </div>
                    <div>
                        <Button type="submit" className='bg-mint' data-close-modal="true" style={{borderRadius: "10px"}}>{t("taxIncomePage.save")}</Button>
                    </div>
                </Fieldset>
            </Form>
        </>
    )
}
