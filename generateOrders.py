from random import randint
from math import ceil
import random as r
import sys

products = ["Bowl", "Salad", "Burrito", "Taco"]
proteins = ["Steak", "Ground Beef", "Chicken", "Vegetables"]
sauces = ["Jalapeno Ranch", "Pineapple Salsa", "Tomatillo Verde Sauce", "Lime Crema", "Guacamole", "Ranch", "Lime Juice", "Chipotle Caesar", "Citrus Vinaigrette", "Sour Cream"]
rice = ["White Rice", "Spanish Rice"]
beans = ["Black Beans", "Pinto Beans"]
cheese = ["Cheddar Cheese", "American Cheese"]
toppings = ["Pico", "Corn", "Lettuce", "Spinach", "Tomatoes", "Onions"]
sides = ["Chips & Salsa", "Chips & Queso", "Chips & Guac"]
drinks = ["16oz Fountain Drink", "32oz Fountain Drink", "Canned Drink"]


#particular strings, when added to order, affects the final price
price_modifiers = {"Steak": 8.29, "Ground Beef": 8.19, "Chicken": 7.29, "Vegetables": 7.29, "Chips & Salsa" : 2.19, "Chips & Queso": 3.64, "Chips & Guac": 3.69, "16oz Fountain Drink":2.25, "32oz Fountain Drink":2.45, "Canned Drink":2.45}


#Attributes of Customer Order = [id, [items], total]
#auth_users is definitely predefined and static

def make_order(id, pSauce=80, pRice=80, pBean=75, pCheese=90, pTopping=99, pSide=40, pDrink=70):
  lst = [id]
  price = 0

  #choose a random product
  lst.append(products[randint(0,3)]) 

  #choose a random protein
  choice = randint(0,3)
  lst.append(proteins[choice]) 
  price += price_modifiers[proteins[choice]]


  #add a random scoop of beans iff randint(1,100) <= pBean
  if (randint(1,100) <= pBean):
    choice = randint(0,len(beans)-1)
    lst.append(beans[choice]) 

  #add a random scoop of rice iff randint(1,100) <= pRice
  if (randint(1,100) <= pRice):
    choice = randint(0,len(rice)-1)
    lst.append(rice[choice]) 

  
  #add a random scoop of cheese iff randint(1,100) <= pCheese
  if (randint(1,100) <= pCheese):
    choice = randint(0,len(cheese)-1)
    lst.append(cheese[choice]) 
  
  #choose [low, high] random sauces iff randint(1,100) <= pSauce
  low = 0
  high = 3
  if (randint(1,100) <= pSauce):
    #ensures different sauces are choosen
    r.shuffle(sauces) 
    for i in range(randint(low,high)):
      lst.append(sauces[i]) 

  #choose [low, high] random toppings iff randint(1,100) <= pTopping
  low = 0
  high = len(toppings)
  if (randint(1,100) <= pTopping):
    r.shuffle(toppings) 
    for i in range(randint(low,high)):
      lst.append(toppings[i]) 


  #add a random side iff randint(1,100) <= pSide
  if (randint(1,100) <= pSide):
    choice = randint(0,len(sides)-1)
    lst.append(sides[choice]) 

    price += price_modifiers[sides[choice]]

  #add a random drink iff randint(1,100) <= pDrink
  if (randint(1,100) <= pDrink):
    choice = randint(0,len(drinks)-1)
    lst.append(drinks[choice]) 

    price += price_modifiers[drinks[choice]]
  
  
  #append price
  lst.append(round(price,2))


  return lst



def addToOrder(lst, item):
  price = lst[-1]
  lst[-1] = item

  if item in price_modifiers:
    price += price_modifiers[item]
  
  lst.append(price)


def lstToStr(lst):
  row = ""
  for i in range(len(lst)-1):
    row += (str(lst[i]) + ",")
  row += (str(lst[-1]) + "\n")
  return row



def main(argv):

  if len(argv) == 1:

    for id in range(1,101):
      orderlst = make_order(id)
      print(lstToStr(orderlst))
    return 


  with open(argv[1], "a") as file:
    overall_price = 0

    for id in range(1,int(argv[2])+1):
      orderlst = make_order(id)
      overall_price += orderlst[-1]
      file.write(lstToStr(orderlst))
    
    print(f"Total Revenue: {round(overall_price,2)}")

  return


if (__name__ == "__main__"):
  main(sys.argv)
