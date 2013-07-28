package betterbreeds.entity;

import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import betterbreeds.entity.ai.EntityAIBeg4;
import betterbreeds.entity.ai.EntityAITamed2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityWolf4 extends EntityTameable
{
	private float field_70926_e;
	private float field_70924_f;

	/** true is the wolf is wet else false */
	private boolean isShaking;
	private boolean field_70928_h;

	/**
	 * This time increases while wolf is shaking and emitting water particles.
	 */
	private float timeWolfIsShaking;
	private float prevTimeWolfIsShaking;

	public EntityWolf4(World par1World)
	{
		super(par1World);
		this.setSize(0.6F, 0.8F);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSit);
		this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, true));
		this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(6, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIBeg4(this, 8.0F));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(3, new EntityAITamed2(this, EntityMob.class, 16.0F, 1, true));
		this.setTamed(false);
	}
	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return spawnBabyAnimal(entityageable);
	}

	protected void func_110147_ax()
	{
		super.func_110147_ax();
		this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);

		if (this.isTamed())
		{
			this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(28.0D);
		}
		else
		{
			this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0D);
		}
	}

	/**
	 * Returns true if the newer Entity AI code should be run
	 */
	public boolean isAIEnabled()
	{
		return true;
	}

	/**
	 * Sets the active target the Task system uses for tracking
	 */ @Override
	 public void setAttackTarget(EntityLivingBase par1EntityLivingBase)
	 {
		 super.setAttackTarget(par1EntityLivingBase);

		 if (par1EntityLivingBase == null)
		 {
			 this.setAngry(false);
		 }
		 else if (!this.isTamed())
		 {
			 this.setAngry(true);
		 }
	 }
	 /**
	  * main AI tick function, replaces updateEntityActionState
	  */@Override
	  protected void updateAITick()
	  {
		  this.dataWatcher.updateObject(18, Float.valueOf(this.func_110143_aJ()));
	  }

	  @Override
	  protected void entityInit()
	  {
		  super.entityInit();
		  this.dataWatcher.addObject(18, new Float(this.func_110143_aJ()));
		  this.dataWatcher.addObject(19, new Byte((byte)0));
		  this.dataWatcher.addObject(20, new Byte((byte)BlockColored.getBlockFromDye(1)));
	  }

	  /**
	   * Plays step sound at given x, y, z for the entity
	   */@Override
	   protected void playStepSound(int par1, int par2, int par3, int par4)
	   {
		   this.playSound("mob.wolf.step", 0.15F, 1.0F);
	   }


	   /**
	    * (abstract) Protected helper method to write subclass entity data to NBT.
	    */
	   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	   {
		   super.writeEntityToNBT(par1NBTTagCompound);
		   par1NBTTagCompound.setBoolean("Angry", this.isAngry());
		   par1NBTTagCompound.setByte("CollarColor", (byte)this.getCollarColor());
	   }

	   /**
	    * (abstract) Protected helper method to read subclass entity data from NBT.
	    */
	   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	   {
		   super.readEntityFromNBT(par1NBTTagCompound);
		   this.setAngry(par1NBTTagCompound.getBoolean("Angry"));
		   if (par1NBTTagCompound.hasKey("CollarColor"))
		   {
			   this.setCollarColor(par1NBTTagCompound.getByte("CollarColor"));
		   }
	   }

	   /**
	    * Determines if an entity can be despawned, used on idle far away entities
	    */
	   protected boolean canDespawn()
	   {
		   return this.isAngry();
	   }

	   /**
	    * Returns the sound this mob makes while it's alive.
	    */@Override
	    protected String getLivingSound()
	    {
	    	return this.isAngry() ? "mob.wolf.growl" : (this.rand.nextInt(3) == 0 ? (this.isTamed() && this.dataWatcher.func_111145_d(18) < 10.0F ? "mob.wolf.whine" : "mob.wolf.panting") : "mob.wolf.bark");
	    }

	    /**
	     * Returns the sound this mob makes when it is hurt.
	     */
	    protected String getHurtSound()
	    {
	    	return "mob.wolf.hurt";
	    }

	    /**
	     * Returns the sound this mob makes on death.
	     */
	    protected String getDeathSound()
	    {
	    	return "mob.wolf.death";
	    }

	    /**
	     * Returns the volume for the sounds this mob makes.
	     */
	    protected float getSoundVolume()
	    {
	    	return 0.4F;
	    }

	    /**
	     * Returns the item ID for the item the mob drops on death.
	     */
	    protected int getDropItemId()
	    {
	    	return -1;
	    }

	    /**
	     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	     * use this to react to sunlight and start to burn.
	     */
	    public void onLivingUpdate()
	    {
	    	super.onLivingUpdate();

	    	if (!this.worldObj.isRemote && this.isShaking && !this.field_70928_h && !this.hasPath() && this.onGround)
	    	{
	    		this.field_70928_h = true;
	    		this.timeWolfIsShaking = 0.0F;
	    		this.prevTimeWolfIsShaking = 0.0F;
	    		this.worldObj.setEntityState(this, (byte)8);
	    	}
	    }

	    /**
	     * Called to update the entity's position/logic.
	     */
	    public void onUpdate()
	    {
	    	super.onUpdate();
	    	this.field_70924_f = this.field_70926_e;

	    	if (this.func_70922_bv())
	    	{
	    		this.field_70926_e += (1.0F - this.field_70926_e) * 0.4F;
	    	}
	    	else
	    	{
	    		this.field_70926_e += (0.0F - this.field_70926_e) * 0.4F;
	    	}

	    	if (this.func_70922_bv())
	    	{
	    		this.numTicksToChaseTarget = 10;
	    	}

	    	if (this.isWet())
	    	{
	    		this.isShaking = true;
	    		this.field_70928_h = false;
	    		this.timeWolfIsShaking = 0.0F;
	    		this.prevTimeWolfIsShaking = 0.0F;
	    	}
	    	else if ((this.isShaking || this.field_70928_h) && this.field_70928_h)
	    	{
	    		if (this.timeWolfIsShaking == 0.0F)
	    		{
	    			this.playSound("mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
	    		}

	    		this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
	    		this.timeWolfIsShaking += 0.05F;

	    		if (this.prevTimeWolfIsShaking >= 2.0F)
	    		{
	    			this.isShaking = false;
	    			this.field_70928_h = false;
	    			this.prevTimeWolfIsShaking = 0.0F;
	    			this.timeWolfIsShaking = 0.0F;
	    		}

	    		if (this.timeWolfIsShaking > 0.4F)
	    		{
	    			float f = (float)this.boundingBox.minY;
	    			int i = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

	    			for (int j = 0; j < i; ++j)
	    			{
	    				float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
	    				float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
	    				this.worldObj.spawnParticle("splash", this.posX + (double)f1, (double)(f + 0.8F), this.posZ + (double)f2, this.motionX, this.motionY, this.motionZ);
	    			}
	    		}
	    	}
	    }

	    @SideOnly(Side.CLIENT)
	    public boolean getWolfShaking()
	    {
	    	return this.isShaking;
	    }

	    @SideOnly(Side.CLIENT)

	    /**
	     * Used when calculating the amount of shading to apply while the wolf is shaking.
	     */
	    public float getShadingWhileShaking(float par1)
	    {
	    	return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * par1) / 2.0F * 0.25F;
	    }

	    @SideOnly(Side.CLIENT)
	    public float getShakeAngle(float par1, float par2)
	    {
	    	float f2 = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * par1 + par2) / 1.8F;

	    	if (f2 < 0.0F)
	    	{
	    		f2 = 0.0F;
	    	}
	    	else if (f2 > 1.0F)
	    	{
	    		f2 = 1.0F;
	    	}

	    	return MathHelper.sin(f2 * (float)Math.PI) * MathHelper.sin(f2 * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
	    }

	    @SideOnly(Side.CLIENT)
	    public float getInterestedAngle(float par1)
	    {
	    	return (this.field_70924_f + (this.field_70926_e - this.field_70924_f) * par1) * 0.15F * (float)Math.PI;
	    }

	    public float getEyeHeight()
	    {
	    	return this.height * 0.8F;
	    }

	    /**
	     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
	     * use in wolves.
	     */
	    public int getVerticalFaceSpeed()
	    {
	    	return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
	    }

	    /**
	     * Called when the entity is attacked.
	     */
	    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
	    {
	    	if (this.isEntityInvulnerable())
	    	{
	    		return false;
	    	}
	    	else
	    	{
	    		Entity var3 = par1DamageSource.getEntity();
	    		this.aiSit.setSitting(false);

	    		if (var3 != null && !(var3 instanceof EntityPlayer) && !(var3 instanceof EntityArrow))
	    		{
	    			par2 = (par2 + 1) / 2;
	    		}

	    		return super.attackEntityFrom(par1DamageSource, par2);
	    	}
	    }

	    public boolean attackEntityAsMob(Entity par1Entity)
	    {
	    	int var2 = this.isTamed() ? 10 : 2;
	    	return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), var2);
	    }

	    /**
	     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
	     */
	    public boolean interact(EntityPlayer par1EntityPlayer)
	    {
	    	ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();

	    	if (this.isTamed())
	    	{
	    		if (itemstack != null)
	    		{
	    			if (Item.itemsList[itemstack.itemID] instanceof ItemFood)
	    			{
	    				ItemFood itemfood = (ItemFood)Item.itemsList[itemstack.itemID];

	    				if (itemfood.isWolfsFavoriteMeat() && this.dataWatcher.func_111145_d(18) < 20.0F)
	    				{
	    					if (!par1EntityPlayer.capabilities.isCreativeMode)
	    					{
	    						--itemstack.stackSize;
	    					}

	    					this.heal((float)itemfood.getHealAmount());

	    					if (itemstack.stackSize <= 0)
	    					{
	    						par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
	    					}

	    					return true;
	    				}
	    			}
	    			else if (itemstack.itemID == Item.dyePowder.itemID)
	    			{
	    				int i = BlockColored.getBlockFromDye(itemstack.getItemDamage());

	    				if (i != this.getCollarColor())
	    				{
	    					this.setCollarColor(i);

	    					if (!par1EntityPlayer.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
	    					{
	    						par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
	    					}

	    					return true;
	    				}
	    			}
	    		}

	    		if (par1EntityPlayer.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote && !this.isBreedingItem(itemstack))
	    		{
	    			this.aiSit.setSitting(!this.isSitting());
	    			this.isJumping = false;
	    			this.setPathToEntity((PathEntity)null);
	    			this.setTarget((Entity)null);
	    			this.setAttackTarget((EntityLivingBase)null);
	    		}
	    	}
	    	else if (itemstack != null && itemstack.itemID == Item.bone.itemID && !this.isAngry())
	    	{
	    		if (!par1EntityPlayer.capabilities.isCreativeMode)
	    		{
	    			--itemstack.stackSize;
	    		}

	    		if (itemstack.stackSize <= 0)
	    		{
	    			par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
	    		}

	    		if (!this.worldObj.isRemote)
	    		{
	    			if (this.rand.nextInt(3) == 0)
	    			{
	    				this.setTamed(true);
	    				this.setPathToEntity((PathEntity)null);
	    				this.setAttackTarget((EntityLivingBase)null);
	    				this.aiSit.setSitting(true);
	    				this.setEntityHealth(20.0F);
	    				this.setOwner(par1EntityPlayer.getCommandSenderName());
	    				this.playTameEffect(true);
	    				this.worldObj.setEntityState(this, (byte)7);
	    			}
	    			else
	    			{
	    				this.playTameEffect(false);
	    				this.worldObj.setEntityState(this, (byte)6);
	    			}
	    		}

	    		return true;
	    	}

	    	return super.interact(par1EntityPlayer);
	    }

	    public void func_70918_i(boolean par1)
	    {
	    	byte var2 = this.dataWatcher.getWatchableObjectByte(19);

	    	if (par1)
	    	{
	    		this.dataWatcher.updateObject(19, Byte.valueOf((byte)1));
	    	}
	    	else
	    	{
	    		this.dataWatcher.updateObject(19, Byte.valueOf((byte)0));
	    	}
	    }

	    @SideOnly(Side.CLIENT)
	    public void handleHealthUpdate(byte par1)
	    {
	    	if (par1 == 8)
	    	{
	    		this.field_70928_h = true;
	    		this.timeWolfIsShaking = 0.0F;
	    		this.prevTimeWolfIsShaking = 0.0F;
	    	}
	    	else
	    	{
	    		super.handleHealthUpdate(par1);
	    	}
	    }

	    @SideOnly(Side.CLIENT)
	    public float getTailRotation()
	    {
	    	return this.isAngry() ? 1.5393804F : (this.isTamed() ? (0.65F - (20.0F - this.dataWatcher.func_111145_d(18)) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F));
	    }

	    /**
	     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
	     * the animal type)
	     */
	    public boolean isBreedingItem(ItemStack par1ItemStack)
	    {
	    	return par1ItemStack == null ? false : (!(Item.itemsList[par1ItemStack.itemID] instanceof ItemFood) ? false : ((ItemFood)Item.itemsList[par1ItemStack.itemID]).isWolfsFavoriteMeat());
	    }

	    /**
	     * Will return how many at most can spawn in a chunk at once.
	     */
	    public int getMaxSpawnedInChunk()
	    {
	    	return 8;
	    }

	    /**
	     * Determines whether this wolf is angry or not.
	     */
	    public boolean isAngry()
	    {
	    	return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
	    }

	    /**
	     * Sets whether this wolf is angry or not.
	     */
	    public void setAngry(boolean par1)
	    {
	    	byte var2 = this.dataWatcher.getWatchableObjectByte(16);

	    	if (par1)
	    	{
	    		this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 2)));
	    	}
	    	else
	    	{
	    		this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -3)));
	    	}
	    }

	    /**
	     * Return this wolf's collar color.
	     */
	    public int getCollarColor()
	    {
	    	return this.dataWatcher.getWatchableObjectByte(20) & 15;
	    }

	    /**
	     * Set this wolf's collar color.
	     */
	    public void setCollarColor(int par1)
	    {
	    	this.dataWatcher.updateObject(20, Byte.valueOf((byte)(par1 & 15)));
	    }

	    /**
	     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
	     */
	    public EntityAnimal spawnBabyAnimal(EntityAgeable par1EntityAgeable)
	    {
	    	int k = rand.nextInt(4);

	    	if (k == 0)
	    	{
	    		EntityWolf4 var3 = new EntityWolf4(this.worldObj);
	    		var3.setOwner(this.getOwnerName());
	    		var3.setTamed(true);
	    		return var3;
	    	}
	    	if (k == 1 )
	    	{
	    		EntityWolf3 var4 = new EntityWolf3(this.worldObj);
	    		var4.setOwner(this.getOwnerName());
	    		var4.setTamed(true);
	    		return var4;
	    	}
	    	if (k > 1 && k <= 3)
	    	{
	    		EntityWolf var2 = new EntityWolf(this.worldObj);
	    		var2.setOwner(this.getOwnerName());
	    		var2.setTamed(true);
	    		return var2;
	    	}

	    	return (EntityAnimal)par1EntityAgeable;
	    }

	    /**
	     * Returns true if the mob is currently able to mate with the specified mob.
	     */
	    public boolean canMateWith(EntityAnimal par1EntityAnimal)
	    {
	    	if (par1EntityAnimal == this)
	    	{
	    		return false;
	    	}
	    	else if (!this.isTamed())
	    	{
	    		return false;
	    	}
	    	else if (!(par1EntityAnimal instanceof EntityWolf4))
	    	{
	    		return false;
	    	}
	    	else
	    	{
	    		EntityWolf4 var2 = (EntityWolf4)par1EntityAnimal;
	    		return !var2.isTamed() ? false : (var2.isSitting() ? false : this.isInLove() && var2.isInLove());
	    	}
	    }

	    public boolean func_70922_bv()
	    {
	    	return this.dataWatcher.getWatchableObjectByte(19) == 1;
	    }
}
